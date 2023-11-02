function clearForm() {
    let inputs = document.getElementsByTagName("input");
    for (let input of inputs) {
        input.value = "";
    }
}
