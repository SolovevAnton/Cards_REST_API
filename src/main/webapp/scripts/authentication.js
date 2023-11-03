function clearForm() {
    let inputs = document.getElementsByTagName("input");
    for (let input of inputs) {
        input.value = "";
    }
}

$(document).ready(
    function () {
        $('#formRegistration').submit(
            function (event) {
                registration();
                event.preventDefault();
            }
        )
    }
);

function registration() {
    if (passwordsMatch()) {
        $.ajax({
            url: 'authentication',
            method: 'POST',
            contentType: "application/json",
            data: JSON.stringify(getUserFromForm()),
            success: successfulLoginHandler,
            error: errorRequestHandler
        })
    } else {
        alert("passwords doesn't match");
    }
}

function passwordsMatch() {
    let pass1 = $('#registrationPassword').val();
    let pass2 = $('#confirmPassword').val();
    return pass1 === pass2;
}

function getUserFromForm() {
    let login = $('#login').val();
    let pass = $('#password').val();
    let name = $('#userName').val();
    return {"login": login, "password": pass, "name": name}
}

$(document).ready(
    function () {
        $('#formSignIn').submit(
            function (event) {
                signIn();
                event.preventDefault();
            }
        )
    }
);


function signIn() {
    let login = $('#login').val();
    let pass = $('#password').val();
    $.ajax({
        url: 'authentication',
        method: 'PUT',
        contentType: "application/json",
        data: JSON.stringify({"login": login, "pass": pass}),
        success: successfulLoginHandler,
        error: errorRequestHandler
    })
}

function successfulLoginHandler() {
    window.location = "/Cards_REST_API"
}

function errorRequestHandler(jqXHR) {
    alert(`Error: ${jqXHR.status} ${jqXHR.responseText}`);
}
