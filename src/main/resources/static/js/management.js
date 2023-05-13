// var myModal = document.getElementById('myModal')
// myModal.addEventListener('shown.bs.modal', function () {

//   })
window.onload = function () {
  //相對路徑宣告
  const baseUrl = window.location.protocol + "//" + window.location.host;
  const contextPath = window.location.pathname.substring(
      0,
      window.location.pathname.indexOf("/", 1)
  );

  //api
  const apiUsers = baseUrl + contextPath + "/users";

  //訊息框
  var toastEl = document.querySelector(".toast");
  var toast = new bootstrap.Toast(toastEl);
  // toast.show(); // 顯示訊息框
  // toast.hide(); // 隱藏訊息框

  //進入頁面檢查是否有token
  let token = localStorage.getItem("token");
  if (!(token == null)) {
    let tokenParts = token.split(".");
    let jwtData = JSON.parse(atob(tokenParts[1]));
    let sub = jwtData.sub;
    $("#user-head").text(sub);
  }

  //刪除選取
  $("#del-selected").click(() => {
    var ids = [];
    $('input[name="id"]:checked').each(function () {
      ids.push($(this).val());
    });
    if (ids.length > 0) {
      let r = confirm("確定要刪除所選項目嗎?");
      if (r) {
        delUser(ids);
      }
    } else {
      $("#toast-msg").text("請選擇要刪除的對象");
      toast.show();
    }
  });

  //新增按鈕
  $("#register").click(function (e) {
    e.preventDefault();
    if (!(token == null)) {
      register();
    } else {
      loginFirst();
    }
  });
  //新增func()
  function register() {
    var myHeaders = new Headers();
    myHeaders.append("Content-Type", "application/json");

    var raw = JSON.stringify({
      account: $("#account").val(),
      username: $("#u-name").val(),
      password: $("#password").val(),
      phone: $("#phone").val(),
    });

    var requestOptions = {
      method: "POST",
      headers: myHeaders,
      body: raw,
      redirect: "follow",
    };
    console.log(apiUsers + "/register");
    fetch(apiUsers + "/register", requestOptions)
        .then((response) => response.json())
        .then((result) => {
          $("#msg").text(result.message);
          if (result.message.includes("註冊會員成功")) {
            $("#register-msg").text("");
            $("#insertModal").modal("hide");
            $("#toast-msg").text("新增成功");
            toast.show();
            $("#search").trigger("click");
          } else {
            $("#register-msg").text(result.message);
          }
        })
        .catch((error) => {
          if (error.message === "401") {
            loginFirst();
          } else {
            console.log("error", error);
          }
        });
  }

//取消按鈕
  $(".cancel").click(()=>{
    $("#register-msg").text("");
    $("#edit-msg").text("");

  })
  //登出按鈕
  $("#logout").click(() => logout());
  //搜尋按鈕
  $("#search").click(() => {
    token = localStorage.getItem("token");
    var myHeaders = new Headers();
    myHeaders.append("Content-Type", "application/json");
    myHeaders.append("AUTHORIZATION", "Bearer " + token);

    var requestOptions = {
      method: "GET",
      headers: myHeaders,
      redirect: "follow",
    };

    fetch(apiUsers + "?name=" + $("#name").val(), requestOptions)
        .then((response) => {
          if (!response.ok) {
            throw new Error(response.status);
          }
          return response.json();
        })
        .then((result) => {
          $(".content-block").html("");
          let list_html = "";
          $.each(result, function (index, item) {
            list_html += ` <div
          class="row single-item ${
                index % 2 == 0 ? "content-odd" : "content-even"
            } ${
                index == result.length - 1 ? "content-last" : ""
            } d-flex justify-content-center align-items-center"
        >
          <div
            class="col-md-3 d-flex justify-content-start align-items-center"
          >
            <div class="row">
              <div class="col-sm-3">
                <input type="checkbox" class="item-id" name="id" value="${
                item.id
            }" />
              </div>
              <div class="col-sm-3">
                          <i class="fa-solid fa-pen-to-square btn-edit "data-bs-toggle="modal"
                          data-bs-target="#editModal"></i>
              </div>
              <div class="col-sm-3">
                <i class="fa-solid fa-trash btn-del"></i>
              </div>
              <div class="col-sm-3"></div>
            </div>
          </div>
          <div class="col-md-3 item-account">${item.account}</div>
          <div class="col-md-3 item-username">${item.username}</div>
          <div class="col-md-3 item-phone">${item.phone}</div>
          <input class="item-version" type="hidden" name="version" value="${
                item.version
            }" />
        </div>`;
          });
          $(".content-block").html(list_html);
        })
        .then(() => {
          edit();
          delSingle();
          $("#clickAll").prop("checked", false);
        })
        .catch((error) => {
          if (error.message === "401") {
            loginFirst();
          } else {
            console.log("error", error);
          }
        });
  });
  //編輯提交
  $("#btn-submit-edit").click((e) => {
    e.preventDefault();
    if((token==null)){
      loginFirst();
    }
    let regex = /^(09)[0-9]{8}$/;
    let phoneNum = $("#phone-edit").val();
    if (regex.test(phoneNum)) {
      editSubmit();
    } else {
      $("#edit-msg").text("請確認電話格式");
    }
  });
  //編輯按鈕
  function edit() {
    $(".btn-edit").click((e) => {
      let target_item = $(e.target).closest(".single-item");
      // console.log($(e.target).closest(".single-item").find(".item-account").html());
      $("#account-edit").val(target_item.find(".item-account").html());
      $("#name-edit").val(target_item.find(".item-username").html());
      $("#phone-edit").val(target_item.find(".item-phone").html());
      $("#version").val(target_item.find(".item-version").val());
      $("#id").val(target_item.find(".item-id").val());
    });
  }
  //刪除按鈕
  function delSingle() {
    $(".btn-del").click((e) => {
      let target_item = $(e.target).closest(".single-item");
      var ids = [];
      ids.push(target_item.find(".item-id").val());
      delUser(ids);
    });
  }
  // 編輯提交 func()
  function editSubmit() {
    token = localStorage.getItem("token");
    var myHeaders = new Headers();
    myHeaders.append("Content-Type", "application/json");
    myHeaders.append("AUTHORIZATION", "Bearer " + token);

    var raw = JSON.stringify({
      id: $("#id").val(),
      account: $("#account-edit").val(),
      username: $("#name-edit").val(),
      phone: $("#phone-edit").val(),
      version: $("#version").val(),
    });

    var requestOptions = {
      method: "PUT",
      headers: myHeaders,
      body: raw,
      redirect: "follow",
    };

    fetch(apiUsers + "/update", requestOptions)
        .then((response) => response.json())
        .then((result) => {
          if (result.message.includes("更新成功")) {
            $("#edit-msg").text("");
            $("#toast-msg").text(result.message);
            $("#editModal").modal("hide");
            toast.show();
            $("#search").trigger("click");
          } else {
            $("#edit-msg").text(result.message);
          }
        })
        .catch((error) => {
          if (error.message === "401") {
            loginFirst();
          } else {
            console.log("error", error);
          }
        });
  }
  // 刪除使用者共用 func()
  function delUser(ids) {
    token = localStorage.getItem("token");

    var myHeaders = new Headers();
    myHeaders.append("AUTHORIZATION", "Bearer " + token);
    myHeaders.append("Content-Type", "application/json");
    var raw = JSON.stringify(ids);

    var requestOptions = {
      method: "DELETE",
      headers: myHeaders,
      body: raw,
      redirect: "follow",
    };

    fetch(apiUsers, requestOptions)
        .then((response) => response.json())
        .then((result) => {
          $("#toast-msg").text(result.message);
          toast.show();
          $("#search").trigger("click");
        })
        .catch((error) => {
          if (error.message === "401") {
            loginFirst();
          } else {
            console.log("error", error);
          }
        });
  }
  //登出func()
  function logout() {
    var myHeaders = new Headers();
    var requestOptions = {
      method: "POST",
      headers: myHeaders,
      redirect: "follow",
    };

    fetch(apiUsers + "/logout", requestOptions)
        .then((response) => response.text())
        .then((result) => {
          console.log(result);
          localStorage.removeItem("token");
          alert("登出成功");
          location.href = "../login.html";
        })
        .catch((error) => {
          alert("請稍後再試一次");
        });
  }
  //請先登入警告func()
  function loginFirst() {

    alert("請先登入");
    location.href = baseUrl + contextPath + "/login.html";
  }

  //全勾選框
  $(document).on("change", 'input[name="clickAll"]', function () {
    // 判斷目前勾選狀態
    var checked = $(this).prop("checked");
    // 將其他同名的 checkbox 設定為相同勾選狀態
    $('input[name="id"]').prop("checked", checked);
  });
  //顯示全資料
  $("#search").trigger("click");
};
