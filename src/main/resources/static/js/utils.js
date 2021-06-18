/**
 * Created by Administrator on 2017/10/12 0012.
 */
/*
<!-- 先引入 eleme的样式 -->
document.write('<link rel="stylesheet" href="https://unpkg.com/element-ui/lib/theme-chalk/index.css" crossorigin="anonymous">');
<!-- 先引入 Vue -->
document.write('<script src="https://unpkg.com/vue/dist/vue.js" crossorigin="anonymous"></script>');
<!-- 引入组件库 -->
document.write('<script src="https://unpkg.com/element-ui/lib/index.js" crossorigin="anonymous"></script>');
/!*引入网络资源文件*!/
document.write('<script src="https://cdn.bootcss.com/vue-resource/1.3.4/vue-resource.min.js" crossorigin="anonymous"></script>');
*/
var __SERVER_IP="118.178.239.92";
var __SERVER_PORT="9091";

function utils_(){
    /*this.get=function (url,data={},options={async:true}) {
        // async:true//默认使用异步，true为异步，false同步
        return this.ajax("GET", url, data, options)
    },*/
    this.post=function (url, data={},options={async:true}) {
        return this.ajax("POST", url, data, options)
    }
    /*this.delete=function (url, data={},options={async:true}) {
        return this.ajax("DELETE", url, data, options)
    }*/
    
    this.ajax=function (methods,url, data={},options={async:true}) {
    	console.log(url);
        return new Promise(function (resolve, reject) {
        	methods = methods || 'POST';         
        	var xmlHttp = null;         
        	if (XMLHttpRequest) {           
        		xmlHttp = new XMLHttpRequest();         
        	} else {           
        		xmlHttp = new ActiveXObject('Microsoft.XMLHTTP');         
        	};         
        	var params = [];         
        	for (var key in data){           
        		if(!!data[key] || data[key] === 0){             
        			params.push(key + '=' + data[key]);           
        		}         
        	};         
        	var postData = params.join('&');         
        	if (methods.toUpperCase() === 'POST') {           
        		xmlHttp.open('POST', url, options.async);           
        		xmlHttp.setRequestHeader("X-Requested-With", "XMLHttpRequest");
        		xmlHttp.setRequestHeader("Content-type", "application/json;charset=UTF-8");
        		xmlHttp.send(JSON.stringify(data));         
        	}else if (methods.toUpperCase() === 'GET') {           
        		xmlHttp.open('GET', url + '?' + postData, options.async);           
        		xmlHttp.send(null);         
        	}else if(methods.toUpperCase() === 'DELETE'){           
        		xmlHttp.open('DELETE', url + '?' + postData, options.async);           
        		xmlHttp.send(null);         
        	}         
        	xmlHttp.onreadystatechange = function () {
        		if (xmlHttp.readyState == 4 ) {
        			if(xmlHttp.status == 200){
        				let responseJson = JSON.parse(xmlHttp.responseText);
        				if(responseJson.code && (responseJson.code+"").indexOf("401.")!=-1){
        					window.alert("登录已过期，请重新登录");
    						utils.openPopWin('登录','/pages/popLogin.html?v='+new Date().getTime(),800,600,function(){
        	                });
        				}else{
        					resolve(responseJson);    
        				}
        				       
        			}else{
            			reject(JSON.parse(xmlHttp.responseText));
            		} 
        		}        
        	}; 
        });
    }

    /*this.ajax=function (url,data,options) {
        return new Promise(function(resolve,reject){
            var url_=url;
            if(url_.indexOf("?")!=-1){
                url_=url_+"&v="+new Date();
            }else{
                url_=url_+"?v="+new Date();
            }
            Vue.http.post("http://"+window.location.host+url_, data, {
                headers: {
                    "X-Requested-With": "XMLHttpRequest",
                }
            }).then(function(res){
                var responseData = res.body;
                resolve(responseData);
            },function(res){
                reject(res);
            });
        });
    }*/

    this.getRequest = function () {
        var url = location.search; //获取url中"?"符后的字串
        var theRequest = new Object();
        if (url.indexOf("?") != -1) {
            var str = url.substr(1);
            strs = str.split("&");
            for(var i = 0; i < strs.length; i ++) {
                theRequest[strs[i].split("=")[0]]=unescape(strs[i].split("=")[1]);
            }
        }
        return theRequest;
    }

    this.initCascader=function(obj){
        let parentCodes = [];
        for(let i=0;i<obj.length;i++){
            parentCodes.push(obj[i].code);
        }
        return this.post("/api/select/initCascader",{parentCodes:parentCodes}).then(res=>{//debugger;
            for(let i=0;i<obj.length;i++){
                let tmp = res.data[obj[i].code];
                for(let j=0;j<tmp.length;j++){
                    obj[i].model.push(tmp[j]);
                }
            }
        });
    }
    this.initSelect=function(obj){

        let params = [];
        for(let i=0;i<obj.length;i++){
            let tmp ={
                code:obj[i].code,
                type:obj[i].type
            }
            params.push(tmp);
        }
        return this.post("/api/select/initSelect",{params:params}).then(res=>{
            for(let i=0;i<obj.length;i++){
                let tmp = res.data[obj[i].type+"_"+obj[i].code];
                for(let j=0;j<tmp.length;j++){
                    obj[i].model.push(tmp[j]);
                }
            }
        });
    }
    
    this.confirm=function(msg,vm,fun1,fun2){
    	vm.$confirm(msg, '提示', {
    		confirmButtonText: '确定',
    		cancelButtonText: '取消',
    		type: 'warning'
     	}).then(() => {
     		fun1();
     	}).catch(() => {
     		fun2();
     	});
    }
    
    
    this.alert=function(msg,vm,fun){
    	vm.$alert(msg, {
	        confirmButtonText: '确定',
	        callback: action => {
	          fun();
	        }
	      });
    }

    this.msg = function(res,vm){
    	if(res.code==1){
    		utils.successMsg(res.msg, vm);
    	}else{
    		utils.errorMsg(res.msg, vm);
    	}
    }
    this.errorMsg = function(msg,vm){
    	vm.$message({
            message: msg,
            type: 'error',
            duration: 2000
        });
    }
    this.successMsg = function(msg,vm){
    	vm.$message({
            message: msg,
            type: 'success',
            duration: 1000
        });
    }
    this.warnMsg = function(msg,vm){
    	vm.$message({
    		message: msg,
    		type: 'warning',
    		duration: 2000
    	});
    }
    this.closePopWin=function(){
        let i = document.getElementById("popwin_i_");
        i.click();
    }
    //弹窗
    this.openPopWin=function(title,src,width,height,onClose) {//debugger;
        let div1, div2, div3, span, div4, iframe;
        if(width>document.body.clientWidth){
        	width=document.body.clientWidth-10;
        }
        if(height>document.body.clientHeight){
        	height=document.body.clientHeight-10;
        }
        div1 = document.getElementById("popwin_div1_");
        if (!div1) {
            div1 = document.createElement("div");
            div1.setAttribute("class", "popWin");
            div1.setAttribute("id", "popwin_div1_");
            div2 = document.createElement("div");
            div2.setAttribute("class", "popWinBox");
            div2.setAttribute("style", "width: " + width + "px;height: " + height + "px;");
            div2.setAttribute("id", "popwin_div2_");
            div3 = document.createElement("div");
            div3.setAttribute("class", "popWinHeader");
            div3.setAttribute("id", "popwin_div3_");
            span = document.createElement("span");
            span.innerText = title;
            span.setAttribute("id", "popwin_span_");
            i = document.createElement("i");
            i.setAttribute("class", "el-icon-close");
            i.setAttribute("id", "popwin_i_");
            div4 = document.createElement("div");
            div4.setAttribute("class", "popWinContent");
            div4.setAttribute("id", "popwin_div4_");
            iframe = document.createElement("iframe");
            iframe.setAttribute("src", src);
            iframe.setAttribute("style", "width: 100%;height: 100%;border: 0");
            iframe.setAttribute("id", "popwin_iframe_");

            div3.appendChild(span);
            div3.appendChild(i);
            div4.appendChild(iframe);
            div2.appendChild(div3);
            div2.appendChild(div4);
            div1.appendChild(div2);

            document.body.appendChild(div1);
        } else {
            div2 = document.getElementById("popwin_div2_");
            span = document.getElementById("popwin_span_");
            i = document.getElementById("popwin_i_");
            iframe = document.getElementById("popwin_iframe_");

            div2.setAttribute("style", "width: " + width + "px;height: " + height + "px;");
            span.innerText = title;
            iframe.setAttribute("src", src);
        }

        i.addEventListener("click", function () {
            div2.setAttribute("style", "margin-top:-30px;width: " + width + "px;height: " + height + "px;");
            setTimeout(function () {
                div1.setAttribute("style", "visibility:hidden;opacity:0");
                span.innerText = "";
                iframe.setAttribute("src", "");
            }, 400);
            if (typeof onClose === 'function') {
                onClose();
            }
        });

        div1.setAttribute("style", "visibility:visible;opacity:1");
        setTimeout(function () {
            div2.setAttribute("style", "margin-top:0px;width: " + width + "px;height: " + height + "px;");
        }, 1);
    }

    this.getParam=function(name){// 获取url指定的参数
        let str = location.search.match(name+"=[^&]+");
        if(str&&str.length>0){
            return str[0].split("=")[1];
        }else{
            return "";
        }
    }
}
var utils = new utils_();

Date.prototype.format = function (fmt) {
    var o = {
        "M+": this.getMonth() + 1, //月份
        "d+": this.getDate(), //日
        "h+": this.getHours(), //小时
        "m+": this.getMinutes(), //分
        "s+": this.getSeconds(), //秒
        "q+": Math.floor((this.getMonth() + 3) / 3), //季度
        "S": this.getMilliseconds(), //毫秒
        "D": "星期" + "天一二三四五六".charAt(this.getDay()) //星期
    };
    if (/(y+)/.test(fmt)) {
        fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    }
    for (var k in o) {
        if (new RegExp("(" + k + ")").test(fmt)) {
            fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
        }
    }
    return fmt;
};
