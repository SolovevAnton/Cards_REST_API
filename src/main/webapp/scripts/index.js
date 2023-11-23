let user_id = getUserIdFromCookie();
let currentUser = getCurrentUser();
let currentCategoryId = 0;
const homeURL = "http://localhost:8080/Cards_REST_API/";
const buttonForAddingCategoryHTML = `<button onClick="addModalCategory()">+Add</button>`;

function getUserIdFromCookie() {
    let cookies = document.cookie.split(' ');
    for (let cookie of cookies) {
        if (cookie.match("id=.+")) {
            return cookie.split('=')[1].replace(";", "");
        }
    }
    return -1;
}

function fillUserCard() {
    createUserCard(currentUser);
}

function getCurrentUser() {
    let currentUser;
    $.ajax({
        type: 'GET',
        async: false,
        url: `users?id=${user_id}`,
        success: function (result) {
            currentUser = result.data;
        },
        error: errorHandler
    })
    return currentUser;
}

function createUserCard(user) {
    $('#userId').text(user.id);
    $('#userLogin').text(user.login);
    $('#userName').text(user.name);
    $('#userRegistrationDate').text(user.registrationDate);
}

function fillTableCategoriesForUser() {
    $("#cardsTableBody").html("");
    $.ajax({
        type: "GET",
        url: `categories?userId=${user_id}`,
        success: function (result) {
            let categories = result.data;
            createCategoriesTable(categories);
        },
        error: errorHandler
    });
}

function createCategoriesTable(categories) {
    let tableHeaders = createHeaders("Category id", "Category name", "Cards", buttonForAddingCategoryHTML);
    let tableBody = '<tbody>';
    for (let category of categories) {
        let categoryModifyButton = `<button  onclick="modifyModalCategory(${category.id},'${category.name}')">modify</button>`;
        let deleteCategoryButtonHTML = `<button id="deleteCategoryBtn" onclick="deleteCategory(${category.id})">delete</button>`;

        tableBody += `<tr>
<td>${category.id}</td>
<td>${category.name}</td>
<td>${createCardsTable(category.id)}</td>
<td>${categoryModifyButton}${deleteCategoryButtonHTML}</td>
</tr>`;
    }
    tableBody += '</tbody>';
    $('#categoriesTable').append(tableHeaders).append(tableBody);
}
function addModalCategory(){
    currentCategoryId = 0;
    $('#add_category_name').val("");
    $('#modalAddCategory').show();
}
function modifyModalCategory(categoryId,categoryName){
    currentCategoryId = categoryId;
    $('#add_category_name').val(categoryName);
    $('#modalAddCategory').show();
}

function createHeaders(...headers) {
    let resultRow = '<tr>'
    for (let header of headers) {
        resultRow += `<th>${header}</th>`;
    }
    return resultRow + '</tr>';
}

function createCardsTable(categoryId) {
    let cards = getCardsForCategory(categoryId);
    let tableHeaders = createHeaders("Question", "Answer", "Creation date");
    let tableBody = `<table>${tableHeaders}<tbody>`;
    for (let card of cards) {
        tableBody += `<tr><td>${card.question}</td><td>${card.answer}</td><td>${card.creationDate}</td></tr>`
    }
    return tableBody + '</tbody></table>';
}

function getCardsForCategory(categoryId) {
    let cards;
    $.ajax({
        type: "GET",
        url: `cards?categoryId=${categoryId}`,
        success: function (result) {
            cards = result.data;
        },
        error: errorHandler,
        async: false
    });
    return cards;
}

function errorHandler(e) {
    alert(`error: ${e.status} ${e.statusText} ${e.responseText}`);
}

function sendNewCategory() {
    let name = $('#add_category_name').val();
    if(currentCategoryId === 0) {
        addCategory(name);
    } else {
        modifyCategory(name)
    }
}
function addCategory(categoryName) {
    $.ajax({
        type: "POST",
        url: 'categories',
        contentType: 'application/json',
        data: JSON.stringify({"user": currentUser, "name": categoryName}),
        success: home,
        error: errorHandler
    });
}
function modifyCategory(categoryName){
    $.ajax({
        type: "PUT",
        url: 'categories',
        contentType: 'application/json',
        data: JSON.stringify({"id":currentCategoryId,"user": currentUser, "name": categoryName}),
        success: home,
        error: errorHandler
    });
}
function home(){
    window.location.replace(homeURL);
}
function deleteCategory(id) {
    $.ajax({
        type:"DELETE",
        url: `categories?id=${id}`,
        success: home,
        error: errorHandler
    })
}
function closeModal(){
    currentCategoryId = 0;
    $('#add_category_name').val("");
    $('#modalAddCategory').hide();
}
