<!DOCTYPE html>

<html lang="en">
<head>
    <title>Img2Img</title>
    <meta charset="utf-8"/>
    <meta charset="utf-8">
    <meta name="viewport"
          content="width=device-width,initial-scale=1,minimum-scale=1,maximum-scale=1,user-scalable=no"/>
    <link href="https://cdn.bootcss.com/bootstrap/3.3.6/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.bootcss.com/bootstrap/3.3.6/css/bootstrap-theme.min.css" rel="stylesheet">
    <script src="https://cdn.bootcss.com/bootstrap/3.3.6/js/bootstrap.min.js"></script>
    <script src="https://cdn.staticfile.org/jquery/3.6.1/jquery.min.js"></script>
    <script src="https://cdn.staticfile.org/jquery.form/4.3.0/jquery.form.min.js"></script>

</head>
<body>
<div class="container">
    <div class="row">
        <div class="panel panel-default">
            <div class="panel-heading">
                <h4 class="panel-title">
                    Img2Img 选择您所要上传的图片(背景为白色效果最好),支持jpg,png格式，大小不要超过12M
                </h4>
            </div>
            <div class="panel-body">
                <form method="POST" enctype="multipart/form-data" action="/img2img/2" id="fileForm" name="form">
                    <div>
                        <p>
                            选择文件<input id="chooseImage" type="file" name="file"/>
                        </p>
                        <p>
                            <input type="button" id="subbut" value="上传"/>
                        </p>
                        <div>
                            上传图片：<img id="cropedBigImg" value='custom' data-address='' width="300px" height="300px" class="img-responsive"/>
                            转换后：<img id="deImg" width="300px" height="300px" class="img-responsive"/>
                        </div>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>
<script>
    $('#chooseImage').on('change',function(){
        var filePath = $(this).val();         //获取到input的value，里面是文件的路径
        var fileFormat = filePath.substring(filePath.lastIndexOf(".")).toLowerCase();
        // 检查是否是图片
        if( !fileFormat.match(/.png|.jpg|.jpeg/) ) {
            error_prompt_alert('上传错误,文件格式必须为：png/jpg/jpeg');
            return;
        }
        var src = window.URL.createObjectURL(this.files[0]); //转成可以在本地预览的格式
        $('#cropedBigImg').attr('src',src);
    });
    $("#subbut").on("click",function(){
        $('#fileForm').ajaxSubmit({
            success : function(data) {
                var src='/downloadFile?fileName='+data.data;
                console.log("src:"+src);
                $('#deImg').attr('src',src);
            },
            beforeSend: function(){
                // 上传前
            },
            error : function(data) {
                //   失败后代码
            }
        })

    });
</script>
</body>

</html>