package com.seckill.web;

import java.util.Date;
import java.util.List;

import javax.swing.LookAndFeel;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.seckill.dto.Exposer;
import com.seckill.dto.SeckillExecution;
import com.seckill.dto.SeckillResult;
import com.seckill.entity.Seckill;
import com.seckill.enums.SeckillStatEnum;
import com.seckill.excepiton.RepeatKillException;
import com.seckill.excepiton.SeckillCloseException;
import com.seckill.excepiton.SeckillException;
import com.seckill.service.SeckillService;

import ch.qos.logback.classic.Logger;

//import ch.qos.logback.classic.Logger;
/**
 * 
 * @author hwj
 *
 */
@Controller
@RequestMapping("/seckill") // url:/模块/资源/{id}/,
public class SeckillController {
	private final Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());
	@Autowired
	private SeckillService seckillServie;

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public String list(Model model) {
		//logger.info("list页面进入");
		// 获取列表页
		List<Seckill> list=seckillServie.getSeckilList();
		model.addAttribute("list",list);
		//logger.info("model={}",model);
		//list.jsp+model=ModelAndView
		return "list";// /WEB-INF/jsp/list.jsp
	}
	
	@RequestMapping(value="/{seckillId}/detail",method=RequestMethod.GET)
	public String detail(@PathVariable("seckillId") Long seckillId,Model model){
		logger.info("detail进入");
		if(seckillId==null){
			return "redirect:/seckill/list";
		}
		Seckill seckill=seckillServie.getById(seckillId);
		if(seckill==null){
			return "forward:/seckill/list";
		}
		model.addAttribute("seckill",seckill);
		logger.info("model={}",model);
		return "detail";
	}
	//ajax  json
	@RequestMapping(value="/{seckillId}/exposer",
					method=RequestMethod.POST,
					produces={"application/json;charset=UTF-8"})
	@ResponseBody
	public SeckillResult<Exposer> exposer(@PathVariable Long seckillId){
		SeckillResult<Exposer> result;
		
		try {
			Exposer exposer=seckillServie.exportSeckillUrl(seckillId);
			result=new SeckillResult<Exposer>(true, exposer);
		} catch (Exception e) {
		//	logger.error(e.getMessage(),e);
			result=new SeckillResult<Exposer>(false, e.getMessage());
		}
		return result;
	}
	@RequestMapping(value="/{seckillId}/{md5}/execution",
					method=RequestMethod.POST,
					produces={"application/json;charset=UTF-8"})
	@ResponseBody
	public SeckillResult<SeckillExecution> execute(@PathVariable("seckillId") long seckillId,
													@PathVariable("md5") String md5,
													@CookieValue(value="killPhone",required=false) Long userPhone){
		logger.info("public SeckillResult<SeckillExecution> execute");
		if(userPhone==null){
			return new SeckillResult<SeckillExecution>(false, "未注册");
		}
		try {
			//SeckillExecution execution=seckillServie.executeSeckill(seckillId, userPhone, md5);
			//存储过程调用
			SeckillExecution execution=seckillServie.executeSeckillProcedure(seckillId, userPhone, md5);
			return new SeckillResult<SeckillExecution>(true, execution);
		} catch (RepeatKillException e) {
			logger.info("重复秒杀");
			SeckillExecution execution=new SeckillExecution(seckillId, SeckillStatEnum.REPEAT_KILL.getState(),SeckillStatEnum.REPEAT_KILL.getStateInfo());
			return new SeckillResult<SeckillExecution>(false, execution);
		} catch (SeckillCloseException e) {
			SeckillExecution execution=new SeckillExecution(seckillId, SeckillStatEnum.END.getState(),SeckillStatEnum.REPEAT_KILL.getStateInfo());
			return new SeckillResult<SeckillExecution>(false, execution);
		} catch (SeckillException e) {
			SeckillExecution execution=new SeckillExecution(seckillId, SeckillStatEnum.INNER_ERROR.getState(),SeckillStatEnum.REPEAT_KILL.getStateInfo());
			return new SeckillResult<SeckillExecution>(false, execution);
		}
	}
	
	@RequestMapping(value="/time/now",method=RequestMethod.GET)
	@ResponseBody
	public SeckillResult<Long> time(){
		logger.info("time");
		Date now =new Date();
		return new SeckillResult<Long>(true, now.getTime());
	}
}
