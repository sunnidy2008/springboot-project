<!DOCTYPE html>
<html>
	<head>
		<meta charset="UTF-8">
		<title>${title}-详细</title>
		<link rel="stylesheet" href="../../css/base.css" />
		<link rel="stylesheet" href="../../css/iconfont.css" />
		<link rel="stylesheet" href="https://unpkg.com/element-ui@2.12.0/lib/theme-chalk/index.css">
		<script src="https://cdn.jsdelivr.net/npm/vue@2.6.0"></script>
		<script src="https://unpkg.com/element-ui@2.12.0/lib/index.js"></script>
		<script src="https://cdn.jsdelivr.net/npm/jquery@3.2.1/dist/jquery.min.js"></script>
		<script src="../../js/utils.js"></script>
	</head>

	<body>
		<div id="app" v-cloak>
			<el-form ref="form" :model="form" :rules="rules" label-width="80px" style="padding:10px">
			  	<#list field_list as item>
			  	<#if item.field_name!=primary_key&&item.field_name!='tCrtTm'&&item.field_name!='tUpdTm'&&item.field_name!='cCrtId'&&item.field_name!='cUpdId'>
	          	<el-form-item label="${item.column_comment}" prop="${item.field_name}">
	            	<#if item.dom_type=='datetime'||item.dom_type=='date'>
	            	<el-date-picker v-model="form.${item.field_name}" type="datetime" placeholder="选择日期时间" style="width:100%"></el-date-picker>
	            	<#elseif item.dom_type=='radio'>
	            	<el-radio-group v-model="form.${item.field_name}" >
	           			<el-radio v-for="item in options.${item.field_name}" :label="item.code">{{item.text}}</el-radio>
	           		</el-radio-group>
	           		<#elseif item.dom_type=='checkbox'>
	            	<el-checkbox-group v-model="form.${item.field_name}">
	            		<el-checkbox v-for="item in options.${item.field_name}" :label="item.code" :key="item.code">{{item.text}}</el-checkbox>
	            	</el-checkbox-group>
	            	<#elseif item.dom_type=='select'>
	            	<el-select v-model="form.${item.field_name}" placeholder="请选择" clearable style="width:100%">
	            		<el-option v-for="item in options.${item.field_name}" :label="item.text" :key="item.code" :value="item.code"></el-option>
	            	</el-select>
	            	<#else>
	            	<el-input v-model="form.${item.field_name}" placeholder="${item.column_comment}" clearable <#if item.character_maximum_length??>maxlength="${item.character_maximum_length}" show-word-limit</#if> ></el-input>
	            	</#if>
	           	</el-form-item>
	           	</#if>
	          	</#list>
			  	<el-form-item>
			    	<el-button type="primary" @click="onSubmit">提交</el-button>
			    	<el-button @click="onCancel">取消</el-button>
			  	</el-form-item>
			</el-form>
		</div>
	</body>
	
	<script>
		var vm = new Vue({
			el: '#app',
			data: function() {
				return {
				    form:{
				    	<#list field_list as item>
				    	<#if item.dom_type=='checkbox'>
	                	"${item.field_name}" : [],
	                	<#else>
	                	"${item.field_name}" : "",
	                	</#if>
	                	</#list>
				    },
				    rules:{
				    	<#list field_list as item>
			  			<#if item.field_name!=primary_key&&item.field_name!='tCrtTm'&&item.field_name!='tUpdTm'&&item.field_name!='cCrtId'&&item.field_name!='cUpdId'>
	          			${item.field_name}:[
	          				{ required: true, message: '不能为空', trigger: 'change' },	
	          			],
	          			</#if>
	          			</#list>
				    },
				    options:{
				    	<#list field_list as item>
			  			<#if item.dom_type=='radio' ||item.dom_type=='checkbox' ||item.dom_type=='select'>
	          			${item.field_name}:[],
	          			</#if>
	          			</#list>
				    },
				}
			},
			methods: {
				//保存用户信息
				"onSubmit" : function(){
					var that = this;
					this.$refs['form'].validate((valid) => {
			        	if (!valid) {
			            	return false;
			          	}
						utils.post("/api/${model_name}", this.form).then(function (res) {
	                        if (res.code == '1') {// 如果成功请求且code为1
	                        	utils.successMsg(res.msg,that);
	                        	setTimeout(function () {
	                                parent.vm.query(false);
	        						parent.utils.closePopWin();
	                            },1000);
	                        } else {
	                            utils.errorMsg(res.msg,that);
	                        }
	
	                    }, function (res) {
	                        utils.errorMsg(res.msg,that);
	                    });
                    });
				},
				onCancel(){
				  	parent.utils.closePopWin();
				},
				query(id){
					let that = this;
					utils.post("/api/${model_name}/detail",{id:id}).then(function (res) {
                        if (res.code == '1') {// 如果成功请求且code为1
                        	that.form=res.data;
                        	utils.successMsg(res.msg,that);
                        } else {
                            utils.errorMsg(res.msg,that);
                        }

                    }, function (res) {
                        utils.errorMsg(res.msg,that);
                    });
				},
				getOptions(){
	            	let that = this;
	            	//TODO:item.option_key
	            	var params ={
	            	
	            		<#list field_list as item>
			  			<#if item.dom_type=='radio' ||item.dom_type=='checkbox' ||item.dom_type=='select'>
	          			${item.field_name}:'#${item.option_key}',
	          			</#if>
	          			</#list>
	          			
	          			//使用t_sys_common_code中的数据必须以#开头，其他的需在OptionService中实现方法
	            		//cEnabled:'#EFFECT',
	            		//cCss:'#COLUMN_CSS'
	            	};
	            	//是否存在值需要去后台
	            	var hasKey=false;
				    for(var key in params){  
				    	hasKey=true;
				        break;
				    };  
				    if(!hasKey){
				    	return;
				    }
	            	utils.post("/api/sysCommonCode/options",params).then(function (res) {
	            		console.log(JSON.stringify(res))
	                    if (res.code == '1') {// 如果成功请求且code为1
	                        for(var key in params){
	                        	that.options[key]=res.data[key];
	                        }
	                    } else {
	                        utils.errorMsg(res.msg,that);
	                    }
	                }, function (res) {
	                	utils.errorMsg("网络错误，请重试",that);
	                });
	            },
			},
			watch:{

			},
			mounted:function () {
				var that = this;
				this.getOptions();
                let id = utils.getParam("id");
				if(id!=""){
                	this.query(id);
				}
            }
		})
  </script>
</html>