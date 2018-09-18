# xcxcms
微信小程序cms系统 包括Java后台  小程序客户端

微信小程序 cms 项目  服务器端源码

## 服务器
### 采用的是springmvc+mybatis架构
### 主要包含，后台cms管理系统和微信小程序接口

①本地运行
使用ngrok 内网穿透
--ngrok http 8080
小程序端不检验合法域名

②服务器部署-tomcat
服务器配置
```<Connector port="443" protocol="org.apache.coyote.http11.Http11Protocol"
 maxThreads="150" SSLEnabled="true" scheme="https" secure="true"  clientAuth="false" sslProtocol="TLS" keystoreFile="你的keystore路径" keystorePass="生成证书时的口令"  />
port: https的端口,默认8443
clientAuth:设置是否双向验证，默认为false，设置为true代表双向验证keystoreFile
keystoreFile: keystore证书的路径
keystorePass: 生成keystore时的口令```
---------------------------------------------------------------------------------
配置http请求自动转为https请求
```<!--将 默认的redirectPort = "8443" 改为 443，port改为80 ，使用http访问时，将会重定向至443端口-->
<Connector connectionTimeout="20000" port="80" protocol="HTTP/1.1" redirectPort="443"/>
需要修改web.xml
修改server.xml的同级文件web.xml，在<web-app>标签下添加字标签(即与<welcome-file-list>同一级子标签)，具体内如如下
<security-constraint>
		<web-resource-collection>
			<web-resource-name>SSL</web-resource-name>
			<url-pattern>/*</url-pattern>
		</web-resource-collection>
		<user-data-constraint>
			<transport-guarantee>CONFIDENTIAL</transport-guarantee>
		</user-data-constraint>
</security-constraint>```


注意：修改utils下的ConstantPool.java的ip的值
和小程序中的  ROOT_URL: 'https://www.cz.xyz/cms/'
