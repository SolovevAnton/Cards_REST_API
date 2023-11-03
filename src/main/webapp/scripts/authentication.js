function clearForm() {
    let inputs = document.getElementsByTagName("input");
    for (let input of inputs) {
        input.value = "";
    }
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
        success: function () {
            alert("logged")
        },
        error: function (jqXHR) {
            alert(`Error: ${jqXHR.status} ${jqXHR.responseText}`);
        }
    })
}
