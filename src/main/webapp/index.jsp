<%@ page contentType="text/html; charset=UTF-8"%>
<html>
<body>
<h2>Hello World!</h2>

<form name="login" action="/manage/user/login.do" method="post" enctype="application/x-www-form-urlencoded">
    <input type="text" name="username"/>
    <input type="text" name="password"/>
    <button type="submit">登录</button>
</form>

<form name="upload" action="/manage/product/upload.do" method="post" enctype="multipart/form-data">
    <input type="file" accept="image/gif,image/jpeg,image/jpg,image/png,image/svg" name="file"/>
    <input type="submit" value="上传图片">
</form>
</body>
</html>
