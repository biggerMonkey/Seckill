	//存放主要交互逻辑js代码
	//javascript 模块化
	
	var seckill={
		//封装秒杀相关ajax的url
		URL:{
			now:function(){
				return '../time/now';
			},
			exposer:function(seckillId){
				return '/Secondskill/seckill/'+seckillId+'/exposer';
			},
			execution:function(seckillId,md5){
				return '/Secondskill/seckill/'+seckillId+'/'+md5+'/execution';
			}
		},
		handleSeckillkill:function(seckillId,node){
			//处理秒杀逻辑
			node.hide().html('<button class="btn btn-primary btn-lg" id="killBtn">开始秒杀</button>');
			//{}中放参数
			$.post(seckill.URL.exposer(seckillId),{},function(result){
				//在回调函数中，执行交互流程
				if(result&&result['success']){
					var exposer=result['data'];
					if(exposer['exposed']){
						//开启秒杀
						//获取秒杀地址
						var md5=exposer['md5'];
						var killUrl=seckill.URL.execution(seckillId,md5);
						console.log("killUrl="+killUrl);
						//绑定一次点击事件，防止多次点击，防止服务器同时接到多次相同请求
						$('#killBtn').one('click',function(){
							console.log("绑定一次点击事件");
							//绑定执行秒杀请求的操做
							//1.禁用按钮
							$(this).addClass('disable');
							//2：发送请求
							$.post(killUrl,{},function(result){
								if(result && result['success']){
									console.log("result="+result);
									var killResult=result['data'];
									var state=killResult['state'];
									var stateInfo=killResult['stateInfo'];
									console.log("stateInfo="+stateInfo);
									//显示秒杀结果
									node.html('<span class="label label-success">'+stateInfo+'</span>');
								}else{
									var killResult=result['data'];
									var stateInfo=killResult['stateInfo'];
									node.html('<span class="label label-success">'+stateInfo+'</span>');
								}
							});
						});
						node.show();
					}else{
						//未开启秒杀,个人电脑，计时偏差
						var now=exposer['now'];
						var start=exposer['start'];
						var end=exposer['end'];
						//重新计算计时逻辑
						seckill.countdown(seckillId,now,start,end);
					}
				}else{
					console.log("result="+result);
				}
			});
		},
		countdown:function(seckillId,nowTime,startTime,endTime){
			console.log("countdown");
			console.log("nowTime="+nowTime);
			console.log("startTime="+startTime);
			console.log("endTime="+endTime);
			var seckillBox=$('#seckill-box');
			console.log(seckillBox);
			if(nowTime>endTime){
				console.log("秒杀结束");
				//秒杀结束
				seckillBox.html('秒杀结束');
			}else if(nowTime<startTime){
				console.log("秒杀未开始");
				//秒杀未开始
				//计时
				var killTime=new Date(startTime+1000);
				//seckillBox.html('秒杀未开始');
				seckillBox.countdown(killTime,function(event){
					console.log("控制时间格式");
					//控制时间格式
					var format=event.strftime('秒杀计时：%D天  %H时  %M分  %S秒');
					seckillBox.html(format);
				}).on('finish.countdown',function(){
					//获取秒杀地址，控制显示逻辑，执行秒杀
					seckill.handleSeckillkill(seckillId,seckillBox);
				});
			}else{
				console.log("正在秒杀");
				seckill.handleSeckillkill(seckillId,seckillBox);
			}
		},
		//验证手机号
		validatePhone:function(phone){
			if(phone && phone.length==11&& !isNaN(phone)){
				return true;
			}else{
				return false;
			}
		},
		//详情页秒杀逻辑
		detail:{
			//详情页初始化
			init:function(params){
				console.log("test2");
				//用户手机验证和登陆，计时交互
				//规划交互流程
				//在cookie中查找手机号
				var killPhone=$.cookie('killPhone');
				console.log(killPhone);
				
				//验证手机号
				if(!seckill.validatePhone(killPhone)){
					console.log("手机号为空");
					//绑定phone
					//控制输出
					var killPhoneModal=$('#killPhoneModal');
					console.log(killPhoneModal);
					//显示弹出层
					killPhoneModal.modal({
						show:true,//显示弹出层
						backdrop:'static',//禁止位置关闭
						keyboard:false
					});
					console.log("弹出层");
					$('#killPhoneBtn').click(function(){
						console.log("click");
						var inputPhone=$('#killPhoneKey').val();
						console.log(inputPhone);
						if(seckill.validatePhone(inputPhone)){
							console.log("电话写入cookie");
							//电话写入cookie
							//$.cookie('killPhone',inputPhone,{expires:7,path:'/seckill'});
							$.cookie('killPhone',inputPhone,{expires:7});
							console.log("写入cookie？？");
							//刷新页面
							window.location.reload();
						}else{
							console.log("不等于");
							$('#killPhoneMessage').hide().html('<label class="label label-danger">手机号错误</label>').show(300);
						}
					});
				}
				var startTime=params['startTime'];
				console.log(startTime);
				var endTime=params['endTime'];
				console.log(endTime);
				var seckillId=params['seckillId'];
				console.log(seckillId);
				$.get(seckill.URL.now(),{},function(result){
					console.log("result="+result['data']);
					if(result&&result['success']){
						var nowTime=result['data'];
						//时间判断
						seckill.countdown(seckillId,nowTime,startTime,endTime);
					}else{
						console.log("result="+result);
					}
				});
			}
		}
	}