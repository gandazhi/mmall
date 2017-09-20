<%@ page contentType="text/html; charset=UTF-8"%>
<html>
<body>
<h2>Hello World!</h2>

<form name="login" action="/manage/user/login.do" method="post" enctype="application/x-www-form-urlencoded">
    <input type="text" name="username"/>
    <input type="text" name="password"/>
    <button type="submit">登录</button>
</form>

<h3>springMvc普通上传图片</h3>
<form name="upload" action="/manage/product/upload.do" method="post" enctype="multipart/form-data">
    <input type="file" accept="image/gif,image/jpeg,image/jpg,image/png,image/svg" name="upload_file"/>
    <input type="submit" value="上传图片">
</form>

<h3>simEditor富文本上传图片</h3>
<form name="upload1" action="/manage/product/rich_text_image_upload.do" method="post" enctype="multipart/form-data">
    <input type="file" accept="image/gif,image/jpeg,image/jpg,image/png,image/svg" name="upload_file"/>
    <input type="submit" value="上传图片"/>
</form>
</body>
</html>
