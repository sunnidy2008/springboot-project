部署说明：
# 【基础】jar包部署--推荐
1、修改utils.js中的服务器地址__SERVER_IP，修改application-docker.yml中的sys.sso.url和sys.server.url，mysql中的用户名和密码，注意需要与docker-compose.yml中mysql的对应

2、使用cmd进入工程的根目录 

3、执行 mvn clean package

4、在根目录的target目录下找到生成的 jar包文件

5、java -jar demo-0.0.1.jar 

6、通过浏览器访问：http://localhost:9091 访问应用


# 【进阶】docker部署
1、修改utils.js中的服务器地址__SERVER_IP，修改application-docker.yml中的sys.sso.url和sys.server.url，mysql中的用户名和密码，注意需要与docker-compose.yml中mysql的对应

2、cmd进入到工程更目录，使用mvn package 打包工程

3、将工程jar包上传到linux服务器的/usr/local/docker/all中

4、将docker-compose.yml上传到/usr/local/docker/all中

5、在linux的/usr/local/docker/all中执行  docker build -t demo:0.0.1 . -f Dockerfile-jar

6、在linux的/usr/local/docker/all中执行docker-compose up -d

7、使用mysql font连接数据库，并将boot2demo.sql导入到数据库中，注意mysql映射出来的宿主机端口为3307

8、找到nginx的映射路径/var/lib/docker/volumes/all_nginxetc/_data，将nginx.conf上传到该路径

9、在/usr/local/docker/all 执行docker-compose restart -d命令

10、使用http://ip 即可访问系统，docker-compose中定义了mysql、redis、两个demo应用、nginx负载

11、注意防火墙打开80nginx， 9091工程对应的外网端口，3307宿主机映射出来的mysql访问端口

系统访问地址：http://opensource.guojiang.club 【具体功能请登录系统后左边菜单访问功能演示】
用户名：admin/123456 user/123456


交流QQ群：709845440 密码：springboot2demo
