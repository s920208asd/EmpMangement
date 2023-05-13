window.onload = function () {
    //相對路徑宣告
    const baseUrl = window.location.protocol + "//" + window.location.host;
    const contextPath = window.location.pathname.substring(
      0,
      window.location.pathname.indexOf("/", 1)
    );
    //api
    const apiUsers = baseUrl + contextPath + "/users";
    $("#register").click(function (e) {
      e.preventDefault();
      console.log(apiUsers + "/register");

      register();
    });
    function register() {
      var myHeaders = new Headers();
      myHeaders.append("Content-Type", "application/json");

      var raw = JSON.stringify({
        account: $("#account").val(),
        username: $("#name").val(),
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
            alert("註冊會員成功");
            location.href="./login.html"
          }
        })
        .catch((error) => {
          $("#msg").text(error.message);
        });
    }
  };