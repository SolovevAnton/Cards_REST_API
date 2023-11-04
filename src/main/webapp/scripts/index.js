let user_id = getUserIdFromCookie();

function fillTableCategoriesForUser() {
    $("#cardsTableBody").html("");
    $.ajax({
        type: "GET",
        url: `categories?userId=${user_id}`,
        success: function (result) {
            let categories = result.data;
            createCategoriesTable(categories);
        },
        error: [function (e) {
            alert(`error: ${e.status} ${e.statusText}`);
        }]
    });
}
function createCategoriesTable(categories){
    let tableHeaders = createHeaders("Category id","Category name","Cards");
    let tableBody ='<tbody>' ;
    for(let category of categories){
        tableBody += `<tr><td>${category.id}</td><td>${category.name}</td><td>${createCardsTable(category.id)}</td></tr>`;
    }
    tableBody += '</tbody>';
    $('#categoriesTable').append(tableHeaders).append(tableBody);
}
function createHeaders(...headers){
    let resultRow = '<tr>'
    for(let header of headers){
        resultRow += `<th>${header}</th>`;
    }
    return resultRow + '</tr>';
}

function createCardsTable(categoryId){
    let cards = getCardsForCategory(categoryId);
    let tableHeaders = createHeaders("Question","Answer","Creation date");
    let tableBody = `<table>${tableHeaders}<tbody>`;
    for(let card of cards){
        tableBody += `<tr><td>${card.question}</td><td>${card.answer}</td><td>${card.creationDate}</td></tr>`
    }
    return tableBody + '</tbody></table>';
}
function getCardsForCategory(categoryId){
    let cards;
    $.ajax({
        type: "GET",
        url: `cards?categoryId=${categoryId}`,
        success: function (result) {
            cards = result.data;
        },
        error: [function (e) {
            alert(`error: ${e.status} ${e.statusText}`);
        }],
        async: false
    });
    return cards;
}
function getUserIdFromCookie() {
    let cookies = document.cookie.split(' ');
    for (let cookie of cookies) {
        if (cookie.match("id=.+")) {
            return cookie.split('=')[1].replace(";","");
        }
    }
    return -1;
}
