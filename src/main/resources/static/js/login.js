window.onload = function () {
    //相對路徑宣告
    const baseUrl = window.location.protocol + "//" + window.location.host;
    const contextPath = window.location.pathname.substring(
        0,
        window.location.pathname.indexOf("/", 1)
    );
    //api
    const apiUsers = baseUrl + contextPath + "/users";
    $("#login").click(function (e) {
        e.preventDefault();
        login();
    });

    function login() {
        var myHeaders = new Headers();
        myHeaders.append("Content-Type", "application/json");

        var raw = JSON.stringify({
            account: $("#account").val(),
            password: $("#password").val(),
        });

        var requestOptions = {
            method: "POST",
            headers: myHeaders,
            body: raw,
        };

        fetch(apiUsers + "/login", requestOptions)
            .then((response) => response.json())
            .then((result) => {
                console.log(result);
                if (result.message) {
                    $("#msg").text("");
                    $("#msg").text(result.message);
                } else {
                    localStorage.setItem("token", result.token);
                    location.href = baseUrl + contextPath + "/management.html";
                }
            })
            .catch((error) => console.log("error", error));
    }
};
