<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>${title}-列表</title>
    <link rel="stylesheet" href="../../css/base.css"/>
	<link rel="stylesheet" href="https://unpkg.com/element-ui@2.12.0/lib/theme-chalk/index.css">
    <script src="https://cdn.jsdelivr.net/npm/vue@2.6.0"></script>
    <script src="https://unpkg.com/element-ui@2.12.0/lib/index.js"></script>
	<script src="https://cdn.jsdelivr.net/npm/jquery@3.2.1/dist/jquery.min.js"></script>
	<script src="../../js/utils.js"></script>
</head>
<body>
	<div id="app" v-cloak>
	    <div class="mainBox height100" style="padding-bottom: 0;">
	        <div class="layoutUi">
	            <el-form :inline="true">
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
		            	<el-select v-model="form.${item.field_name}" placeholder="${item.column_comment}" clearable style="width:100%">
		            		<el-option v-for="item in options.${item.field_name}" :label="item.text" :key="item.code" :value="item.code"></el-option>
		            	</el-select>
		            	<#else>
		            	<el-input v-model="form.${item.field_name}" placeholder="${item.column_comment}" clearable <#if item.character_maximum_length??>maxlength="${item.character_maximum_length}" show-word-limit</#if> ></el-input>
		            	</#if>
		           	</el-form-item>
		           	</#if>
		          	</#list>
	                <el-form-item>
	                    <el-button type="primary" @click="form._pageNum=1;query()">查询</el-button>
						<el-button type="primary" @click="addNew" plain>新增</el-button>
	                </el-form-item>
	            </el-form>
	        </div>
	        <el-table :data="tableData.resultList" :height="tableHeight" style="width: 100%">
	            <!--数据区域-->
	            <el-table-column prop="${primary_key}" label="主键" v-if="false"></el-table-column>
	            <#list field_list as item>
	            <#if item.dom_type=='radio'||item.dom_type=='select'>
	            <el-table-column label="${item.column_comment}" width="200">
	            	<template slot-scope="scope">
						<span v-for="item in options.${item.field_name}" size="mini" :class="'column__'+item.css" round  v-if="scope.row.${item.field_name}==item.code" class="badge" v-html="item.text"></span>
					</template>
	            </el-table-column>
	            <#elseif item.field_name!=primary_key&&item.field_name!='tCrtTm'&&item.field_name!='tUpdTm'&&item.field_name!='cCrtId'&&item.field_name!='cUpdId'>
	            <el-table-column prop="${item.field_name}" label="${item.column_comment}" width="200"></el-table-column>
	            </#if>
	            </#list>
	            <el-table-column
			      fixed="right"
			      label="操作"
			      width="120">
			      <template slot-scope="scope">
			        <el-button @click="detail(scope.row)" type="text" size="small">查看/修改</el-button>
			        <el-button @click="del(scope.row)" type="text" size="small">删除</el-button>
			      </template>
			    </el-table-column>
	        </el-table>
	        <el-pagination :current-page="form._pageNum" :page-sizes="[10,20, 50, 100]" :page-size="form._pageSize"
	                       @current-change="handleCurrentChange"
	                       @size-change="handleSizeChange"
	                       layout="total, sizes, prev, pager, next, jumper" :total="tableData.total">
	        </el-pagination>
	    </div>
	</div>
</body>
</html>
<script>
    var vm=new Vue({
        el: '#app',
        data: function () {
            return {
                tableData: {
                	resultList:[]
                },
                //查询表单上的字段
                form: {
                	<#list field_list as item>
			    	<#if item.dom_type=='checkbox'>
                	"${item.field_name}" : [],
                	<#else>
                	"${item.field_name}" : "",
                	</#if>
                	</#list>
                    _count: true,// 是否进行统计
                    _pageNum: 1,// 当前页面
                    _pageSize: 10,// 当前页面数据条数
                    _total: 0,//总数
                },
                options:{
			    	<#list field_list as item>
		  			<#if item.dom_type=='radio' ||item.dom_type=='checkbox' ||item.dom_type=='select'>
          			${item.field_name}:[],
          			</#if>
          			</#list>
			    },
                tableHeight:0,//table的动态高度
            }
        },
        methods: {
			//查询
            query: function (showTips) {
            	//this.form._total = this.tableData.total;
            	var that = this;
            	utils.post("/api/${model_name}/list", this.form).then(function (res) {
                    if (res.code == '1') {// 如果成功请求且code为1
                    	that.tableData = res.data;
                        if(showTips){
                        	utils.successMsg(res.msg,that);
                        }
						//that.form._count = false;// 每次请求完至为false
                    } else {
                        utils.errorMsg(res.msg,that);
                    }
                }, function (res) {
                	utils.errorMsg("网络错误，请重试",that);
                });
            },
            //新增
            addNew:function(){
            	let that = this;
                utils.openPopWin('新增','${model_name}Detail.html',800,600,function(){
                });
            },
            //查看、修改
            detail:function(data){
            	let that = this;
                utils.openPopWin('详细','${model_name}Detail.html?id='+data.${primary_key},800,600,function(){
                });
            },
            //删除
            del:function(data){
            	let that= this;
            	this.$confirm('此操作将永久删除该数据, 是否继续?', '提示', {
		          confirmButtonText: '确定',
		          cancelButtonText: '取消',
		          type: 'warning'
		        }).then(() => {
		        	utils.post("/api/${model_name}/delete",{id:data['${primary_key}']}).then(function (res) {
	                    if (res.code == '1') {// 如果成功请求且code为1
	                        utils.successMsg("删除成功",that);
	                        that.query(false);
							//that.form._count = false;// 每次请求完至为false
	                    } else {
	                        utils.errorMsg(res.msg,that);
	                    }
	                }, function (res) {
	                	utils.errorMsg("网络错误，请重试",that);
	                });
		        }).catch(() => {
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
            // 当显示页面条数发生改变
            handleSizeChange: function (val) {
                this.form._pageSize = val;
                this.query(true);
            },
            // 当当前页面发生改变
            handleCurrentChange: function (val) {
                this.form._pageNum = val;
                this.query(true);
            },
            // 当搜索的条件发生改变
            changeCount:function(){
                this.form._count=true;// 将count为true，重新去统计
            }
        },
        mounted: function () {
        	var that = this;
            //动态设置table的高度
            let layoutUiHeight = document.getElementsByClassName("layoutUi")[0].clientHeight;
            let pagination = document.getElementsByClassName("el-pagination")[0].clientHeight;
            let mainHeight = document.getElementsByClassName("mainBox")[0].clientHeight
            this.$nextTick(function(){
				that.tableHeight = mainHeight-layoutUiHeight-pagination-20;
			});
			this.getOptions();
            this.query(true);// 加载数据
        }
    });
</script>