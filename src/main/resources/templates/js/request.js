var url = "http://127.0.0.1/wb" //本地
// var url= "http://114.116.213.202/wb" //华为
// var url= "http://106.14.200.144/wb" //阿里

function login() {
    var username = document.getElementById("username").value;
    var password = document.getElementById("password").value;
    if (username == "" || password == "") {
        alert("用户名密码不能为空")
        return
    }
    $.ajax({
        url: url + '/sign/doSign',
        type: 'post',
        dataType: 'json',
        contentType: "application/json;charsetset=UTF-8",
        async: false,//异步请求
        cache: false,
        data: JSON.stringify({
            "phoneNumber": username,
            "password": password,
            "signType": 1//密码登录传1
        }),
        xhrFields: {
            withCredentials: true
        },
        crossOrigin: true,
        //执行成功的回调函数
        success: function (data, textStatus, request) {
            if (data.code === 9000) {
                // window.location.href=url+"../templates/main.html";
                var token = request.getResponseHeader("token")
                var userId = data.data.userId
                window.sessionStorage.setItem("token", userId + "|" + token);
                window.location.href = url + "/html/index.html";
                window.event.returnValue = false;
                // window.open(url+"/main.html");
            } else {
                alert(data.message)
            }
        },
        //执行失败或错误的回调函数
        error: function (data) {
            alert("登录失败");
        },
    });
}


function getWebViewInfo() {
    $.ajax({
        url: url + '/business/getWebViewInfo',
        type: 'post',
        dataType: 'json',
        contentType: "application/json;charsetset=UTF-8",
        async: false,//异步请求
        cache: false,
        headers: {
            "token": window.sessionStorage.getItem("token")
        },
        xhrFields: {
            withCredentials: true
        },
        crossOrigin: true,
        //执行成功的回调函数
        success: function (data) {
            if (data.code === 9000) {
                $('#userNumbers').text(data.data.usersNumber)
                $('#skillsNumber').text(data.data.skillsNumber)
                $('#skillSuccNumber').text(data.data.skillSuccNumber)
                $('#logNumber').text(data.data.logNumber)
                //动态添加列表
                var skillList = data.data.skillList
                for (i = 0; i < skillList.length; i++) {
                    var ulogo = skillList[i].user.userLogo
                    var uName = skillList[i].user.userName
                    var dec = skillList[i].serviceDec
                    document.getElementById("skillList").innerHTML += "<a href=\"#\">\n" +
                        "                        <li>\n" +
                        "                        <img src=\"" + ulogo + "\" class=\"profile-img pull-left\">\n" +
                        "                        <div class=\"message-block\">\n" +
                        "                        <div><span class=\"username\">" + uName + "</span> <span\n" +
                        "                class=\"message-datetime\">12 min ago</span>\n" +
                        "                    </div>\n" +
                        "                    <div class=\"message\">" + dec + "\n" +
                        "                    </div>\n" +
                        "                    </div>\n" +
                        "                    </li>\n" +
                        "                    </a>";

                }

            } else if (data.code === -2) {
                alert("用户身份失效，请重新登录")
                toSignPageAndClearToken()
            } else {
                alert(data.message)
            }
        },
        //执行失败或错误的回调函数
        error: function (data) {
            alert("网络异常，请重试");
        }
    });
}

function getLogList() {
    var token = window.sessionStorage.getItem("token")
    if (token == null || token === "") {
        alert("用户身份失效，请重新登录")
        toSignPageAndClearToken()
        return
    }
    $('#logTable').DataTable({
        language: {
            "lengthMenu": '每页显示 <select style="background: #c4e3f3;width: 60px">'
                + '<option value="10">10</option>'
                + '<option value="20">20</option>'
                + '<option value="30">50</option>'
                + '<option value="40">100</option>'
                + '<option value="50">200</option>' + '</select> 条',
            "paginate": {
                "first": "首页",
                "last": "尾页",
                "previous": "上一页",
                "next": "下一页"
            },
            "processing": "加载中...",  //DataTables载入数据时，是否显示‘进度’提示
            "emptyTable": "暂无数据",
            "info": "共 _PAGES_ 页  _TOTAL_ 条数据  ",
            "infoEmpty": "暂无数据",
            "emptyTable": "暂无要处理的数据...",  //表格中无数据
            "search": "搜索:",
            "infoFiltered": " —— 从  _MAX_ 条数据中筛选",
            "zeroRecords": "没有找到记录",
            "processing": true,
            "autoWidth": false

        },
        ajax: {
            url: url + '/business/getLogList',
            type: "post",
            async: false,//异步请求
            cache: false,
            dataType: 'json',
            contentType: "application/json;charsetset=UTF-8",
            async: false,//异步请求
            cache: false,
            headers: {
                "token": token
            },
            xhrFields: {
                withCredentials: true
            },
            crossOrigin: true,
            dataSrc: function (data) {
                return data;
            }
        },
        columns: [
            {
                "data": "id",
                "title": "ID"
            },
            {
                "data": "createTime",
                "title": "创建时间"
            }, {
                "data": "logLever",
                "title": "日志等级"
            }, {
                "data": "exceptionName",
                "title": "错误内容"
            }, {
                "data": "exceptionContent",
                "title": "发生的Class文件路径",
                "render": function (data, type, row) {
                    var da = data.toString().replace(new RegExp(/,/g), "\n")
                    return da
                }
            }, {
                "data": "lineNumber",
                "title": "错误在类文件中的行数",
                "render": function (data, type, row) {
                    var da = data.toString().replace(new RegExp(/,/g), "\n")
                    return da
                }
            },
            {
                "data": "exceptionState",
                "title": "错误状态",
                "render": function (data, type, row) {
                    if (data === 0) {
                        return '<div class="description" style="color: red">未修复</div>'
                    } else {
                        return '<div class="description" style="color: #5cb85c">已修复</div>'
                    }

                }
            }, {
                "data": "exceptionState",
                "title": "操作",

                // "title": "操作",
                "render": function (data, type, row_, meta) {
                    switch (data) {
                        case 0:
                            return data = "<button type=\"button\"  style = \"margin:0px;display:inline\" class=\"btn bt_pass btn-primary btn-xs\" id=\"dt_pass\" onclick=\"fixLogById(" + JSON.stringify(row_).replace(/"/g, '&quot;') + ")\"><i class=\"fa fa-check\"></i> 修改状态 </button>";
                        case 1:
                            // return data = "<button type=\"button\"  style=\"display:inline\" class=\"btn bt_unpass btn-danger btn-xs\"  id=\"dt_unpass\" onclick=\"deleteLogById(" + JSON.stringify(row_).replace(/"/g, '&quot;') + ")\"><i class=\"fa fa-times\" ></i> 删除日志 </button>";
                            return data = "<button type=\"button\"  style=\"display:inline\" class=\"btn bt_unpass btn-danger btn-xs\"  id=\"dt_unpass\" onclick=\"deleteLogById(" + JSON.stringify(row_).replace(/"/g, '&quot;') + ")\"><i class=\"fa fa-times\" ></i> 删除日志 </button>";
                    }


                }
            }
        ]
    });
}

//删除全部日志
function deleteAllLogs() {
    var txt;
    if (confirm("是否删除全部日志，该操作不可撤销")) {
        txt = "确定";
    } else {
        txt = "取消";
    }
    if (txt === "确定") {
        //删除全部日志
        delLog(null, 0)
    }
}

//删除已修复日志
function deleteAleadyFixLogs() {
    delLog(null, 1)
}

//删除指定日志
function deleteLogById(row_) {
    delLog(row_, 2)
}

//标记指定日志为已修复
function fixLogById(row_) {
    $.ajax({
        url: url + '/business/fixLogByLogId',
        type: 'post',
        dataType: 'json',
        contentType: "application/json;charsetset=UTF-8",
        async: false,//异步请求
        cache: false,
        headers: {
            "token": window.sessionStorage.getItem("token")
        },
        data: JSON.stringify({
            "logId": row_.id
        }),
        xhrFields: {
            withCredentials: true
        },
        crossOrigin: true,
        //执行成功的回调函数
        success: function (data, textStatus, request) {
            if (data.code === 9000) {
                $("#logTable").dataTable().fnDestroy();
                getLogList();
            } else if (data.code === -2) {
                alert("用户身份失效，请重新登录")
                toSignPageAndClearToken()
            } else {
                alert(data.message)
            }
        },
        //执行失败或错误的回调函数
        error: function (data) {
            alert("操作失败，网络异常");
        },
    });
}


function delLog(row_, delType) {
    var logId = ""
    try {
        logId = row_.id;
    } catch (e) {
    }
    $.ajax({
        url: url + '/business/deleteLog',
        type: 'post',
        dataType: 'json',
        contentType: "application/json;charsetset=UTF-8",
        async: false,//异步请求
        cache: false,
        headers: {
            "token": window.sessionStorage.getItem("token")
        },
        data: JSON.stringify({
            "logId": logId,
            "delType": delType,
        }),
        xhrFields: {
            withCredentials: true
        },
        crossOrigin: true,
        //执行成功的回调函数
        success: function (data, textStatus, request) {
            if (data.code === 9000) {
                $("#logTable").dataTable().fnDestroy();
                getLogList();
                // switch (delType) {
                //     case 0:
                //         //删除全部日志成功
                //         break;
                //     case 1:
                //         // //删除已修复日志成功
                //         break;
                //     case 2:
                //         //删除指定日志成功
                //         // alert("删除成功")
                //         break;
                // }
            } else if (data.code === -2) {
                alert("用户身份失效，请重新登录")
                toSignPageAndClearToken()
            } else {
                alert(data.message)
            }
        },
        //执行失败或错误的回调函数
        error: function (data) {
            alert("操作失败，网络异常");
        },
    });
}

//解除、封禁用户 解封传2  封禁传 0
function banOrUnBanUserByUserId(row_, doType) {
    $.ajax({
        url: url + '/user/deleteOrBanUser',
        type: 'post',
        dataType: 'json',
        contentType: "application/json;charsetset=UTF-8",
        async: false,//异步请求
        cache: false,
        headers: {
            "token": window.sessionStorage.getItem("token")
        },
        data: JSON.stringify({
            "phoneNumber": row_.phoneNumber,
            "doType": doType
        }),
        xhrFields: {
            withCredentials: true
        },
        crossOrigin: true,
        //执行成功的回调函数
        success: function (data, textStatus, request) {
            if (data.code === 9000) {
                $("#userTable").dataTable().fnDestroy();
                getUserList(0);
            } else if (data.code === -2) {
                alert("用户身份失效，请重新登录")
                toSignPageAndClearToken()
            } else {
                alert(data.message)
            }
        },
        //执行失败或错误的回调函数
        error: function (data) {
            alert("操作失败，网络异常");
        },
    });
}


function getUserList(type) {
    var token = window.sessionStorage.getItem("token")
    if (token == null || token === "") {
        alert("用户身份失效，请重新登录")
        toSignPageAndClearToken()
        return
    }
    $('#userTable').DataTable({
        language: {
            "lengthMenu": '每页显示 <select style="background: #c4e3f3;width: 60px">'
                + '<option value="10">10</option>'
                + '<option value="20">20</option>'
                + '<option value="30">50</option>'
                + '<option value="40">100</option>'
                + '<option value="50">200</option>' + '</select> 条',
            "paginate": {
                "first": "首页",
                "last": "尾页",
                "previous": "上一页",
                "next": "下一页"
            },
            "processing": "加载中...",  //DataTables载入数据时，是否显示‘进度’提示
            "emptyTable": "暂无数据",
            "info": "共 _PAGES_ 页  _TOTAL_ 条数据  ",
            "infoEmpty": "暂无数据",
            "emptyTable": "暂无要处理的数据...",  //表格中无数据
            "search": "搜索:",
            "infoFiltered": " —— 从  _MAX_ 条数据中筛选",
            "zeroRecords": "没有找到记录",
            "processing": true,
            "autoWidth": true

        },
        "destroy": "true",
        "ajax": {
            "url": url + '/user/getAllUsers',
            "type": "post",
            // "data": JSON.stringify({
            //     "doType": type
            // }),
            "contentType": "application/json;charsetset=UTF-8",
            "dataType": 'json',
            "async": true,//异步请求
            "cache": false,
            "headers": {
                "token": token
            },
            "xhrFields": {
                withCredentials: true
            },
            "crossOrigin": true,
            dataSrc: function (data) {
                return data;
            }
        },
        columns: [
            {
                "data": "userLogo",
                "title": "用户头像",
                "render": function (data, type, row) {
                    //<img src="111111" style="border-radius: 10px;width: 50px;height: 50px"/>
                    return "<img src=\"" + row.userLogo + "\" style=\"border-radius: 6px;width: 50px;height: 50px\"/>"

                }
            },
            {
                "data": "userId",
                "title": "用户ID"
            },
            {
                "data": "userName",
                "title": "用户名"
            },
            {
                "data": "userType",
                "title": "用户类型(iOS/Android)"
                ,
                "render": function (data, type, row_) {
                    if (data === 0) {
                        return '<div class="description">安卓</div>'
                    } else {
                        return '<div class="description" >苹果</div>'
                    }
                }
            },
            {
                "data": "phoneNumber",
                "title": "联系电话"
            },
            {
                "data": "sex",
                "title": "性别"
                ,
                "render": function (data, type, row_) {
                    if (data === 0) {
                        return '<div class="description">女</div>'
                    } else if (data === 1) {
                        return '<div class="description" >男</div>'
                    } else {
                        return '<div class="description" >未填写</div>'
                    }
                }
            },
            {
                "data": "wyy_accid",
                "title": "视频通话账号"
            },
            {
                "data": "wyy_token",
                "title": "视频通话密码",
            },
            {
                "data": "indentity",
                "title": "是否已实名",
                "render": function (data, type, row) {
                    if (data == null) {
                        return '<div class="description" style="color: red">未实名</div>'
                    } else {
                        return '<div class="description" style="color: #5cb85c">已实名</div>'
                    }

                }
            }
            ,
            {
                "data": "ban",
                "title": "用户状态",
                "render": function (data, type, row_) {
                    if (data) {
                        return '<div class="description" style="color: red">封禁</div>'
                    } else {
                        return '<div class="description" style="color: #5cb85c">正常</div>'
                    }
                }
            },
            {
                "data": "null",
                "title": "操作",
                "render": function (data, type, row_) {
                    // return  data = "<button type=\"button\"  style = \"margin:0px;display:inline\" class=\"btn bt_pass btn-primary btn-xs\" id=\"dt_pass\" onclick=\"deleteUserByUserId(" + JSON.stringify(row_).replace(/"/g, '&quot;') + ")\"><i class=\"fa fa-check\"></i> 删除用户 </button>" + "<button type=\"button\"  style = \"margin:0px;display:inline\" class=\"btn bt_pass btn-primary btn-xs\" id=\"dt_pass\" onclick=\"banUserByUserId(" + JSON.stringify(row_).replace(/"/g, '&quot;') + ")\"><i class=\"fa fa-check\"></i> 删除用户 </button>";
                    if (row_.ban) {
                        return data = "<button type=\"button\"  style=\"display:inline\" class=\"btn bt_unpass btn-danger2 btn-xs\"  id=\"dt_unpass\" onclick=\"banOrUnBanUserByUserId(" + JSON.stringify(row_).replace(/"/g, '&quot;') + ",2)\"><i class=\"fa fa-unlock\" aria-hidden=\"false\"\" ></i> 解除封禁 </button>";
                    } else {

                        return data = "<button type=\"button\"  style=\"display:inline\" class=\"btn bt_unpass btn-danger btn-xs\"  id=\"dt_unpass\" onclick=\"banOrUnBanUserByUserId(" + JSON.stringify(row_).replace(/"/g, '&quot;') + ",0)\"><i class=\"fa fa-unlock-alt\" aria-hidden=\"true\"\" ></i> 封禁用户 </button>";
                    }
                }
            }
            // ,
            // {
            //     "data": "exceptionState",
            //     "title": "操作",
            //
            //     // "title": "操作",
            //     "render": function (data, type, row_, meta) {
            //         switch (data) {
            //             case 0:
            //                 return data = "<button type=\"button\"  style = \"margin:0px;display:inline\" class=\"btn bt_pass btn-primary btn-xs\" id=\"dt_pass\" onclick=\"fixLogById(" + JSON.stringify(row_).replace(/"/g, '&quot;') + ")\"><i class=\"fa fa-check\"></i> 修改状态 </button>";
            //             case 1:
            //                 // return data = "<button type=\"button\"  style=\"display:inline\" class=\"btn bt_unpass btn-danger btn-xs\"  id=\"dt_unpass\" onclick=\"deleteLogById(" + JSON.stringify(row_).replace(/"/g, '&quot;') + ")\"><i class=\"fa fa-times\" ></i> 删除日志 </button>";
            //                 return data = "<button type=\"button\"  style=\"display:inline\" class=\"btn bt_unpass btn-danger btn-xs\"  id=\"dt_unpass\" onclick=\"deleteLogById(" + JSON.stringify(row_).replace(/"/g, '&quot;') + ")\"><i class=\"fa fa-times\" ></i> 删除日志 </button>";
            //         }
            //
            //
            //     }
            // }
        ]
    });
}

function toLogPage() {
    window.location.href = url + "/html/table/datatable.html";
}

function toUserListPage() {
    window.location.href = url + "/html/table/datatable_user.html";
}


function toMainPage() {
    window.location.href = url + "/html/index.html";
}


function toSignPageAndClearToken() {
    window.sessionStorage.clear()
    window.location.href = url + "/html/login.html";
}

function showAlert(msg) {
    var txt;
    if (confirm(msg)) {
        txt = "您按了确定";
    } else {
        txt = "您按了取消";
    }
}