let user_id = getUserIdFromCookie();
let currentUser = getCurrentUser();
let currentCategoryId = 0;
let currentCardId = 0;
const homeURL = "http://localhost:8080/Cards_REST_API/";


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
        url: `users/${user_id}`,
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

function createCategoriesTable(categories) {
    const buttonForAddingCategoryHTML = `<button onClick='addModalCategory()'>+Add</button>`;
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

function addModalCategory() {
    currentCategoryId = 0;
    $('#modalNameCategory').text("Add new");
    $('#add_category_name').val("");
    $('#modalAddCategory').show();
}

function modifyModalCategory(categoryId, categoryName) {
    currentCategoryId = categoryId;
    $('#modalNameCategory').text("Modify category");
    $('#add_category_name').val(categoryName);
    $('#modalAddCategory').show();
}

function sendNewCategory() {
    let name = $('#add_category_name').val();
    if (currentCategoryId === 0) {
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

function modifyCategory(categoryName) {
    $.ajax({
        type: "PUT",
        url: 'categories',
        contentType: 'application/json',
        data: JSON.stringify({"id": currentCategoryId, "user": currentUser, "name": categoryName}),
        success: home,
        error: errorHandler
    });
}

function deleteCategory(id) {
    $.ajax({
        type: "DELETE",
        url: `categories?id=${id}`,
        success: home,
        error: errorHandler
    })
}

function createHeaders(...headers) {
    let resultRow = '<tr>'
    for (let header of headers) {
        resultRow += `<th>${header}</th>`;
    }
    return resultRow + '</tr>';
}

function getCurrentCategory() {
    let currentCategory;
    $.ajax({
        type: 'GET',
        async: false,
        url: `categories?id=${currentCategoryId}`,
        success: function (result) {
            currentCategory = result.data;
        },
        error: errorHandler
    })
    return currentCategory;
}

function createCardsTable(categoryId) {
    const buttonForAddingHTML = `<button onClick="addModalCard(${categoryId})">+Add</button>`;
    let cards = getCardsForCategory(categoryId);
    let tableHeaders = createHeaders("Question", "Answer", "Creation date", buttonForAddingHTML);
    let tableBody = `<table>${tableHeaders}<tbody>`;
    for (let card of cards) {
        let deleteCardButtonHTML = `<button onclick="deleteCard(${card.id})">delete</button>`;
        let modifyCardButtonHTML = `<button onclick="modifyModalCard(${categoryId},${card.id},'${card.question}','${card.answer}')">modify</button>`;
        tableBody += `<tr>
<td>${card.question}</td>
<td>${card.answer}</td>
<td>${card.creationDate}</td>
<td>${deleteCardButtonHTML}${modifyCardButtonHTML}</td>
</tr>`
    }
    return tableBody + '</tbody></table>';
}

function deleteCard(id) {
    $.ajax({
        type: "DELETE",
        url: `cards?id=${id}`,
        success: home,
        error: errorHandler
    })
}

function addModalCard(categoryId) {
    currentCardId = 0;
    currentCategoryId = categoryId;
    $('#modalNameCard').text("Add new");
    $('#add_card_question').val("");
    $('#add_card_answer').val("");
    $('#modalAddCard').show();
}

function modifyModalCard(categoryId, cardId, question, answer) {
    currentCardId = cardId;
    currentCategoryId = categoryId;
    $('#modalNameCard').text("Modify card");
    $('#add_card_question').val(question);
    $('#add_card_answer').val(answer);
    $('#modalAddCard').show();
}

function sendCard() {
    let question = $('#add_card_question').val();
    let answer = $('#add_card_answer').val();
    let category = getCurrentCategory();
    let action = currentCardId === 0 ? addCard : modifyCard;

    action(question, answer, category);
}

function addCard(question, answer, category) {
    $.ajax({
        type: "POST",
        url: 'cards',
        contentType: 'application/json',
        data: JSON.stringify({"category": category, "question": question, "answer": answer}),
        success: home,
        error: errorHandler
    });
}

function modifyCard(question, answer, category) {
    $.ajax({
        type: "PUT",
        url: 'cards',
        contentType: 'application/json',
        data: JSON.stringify({"id": currentCardId, "category": category, "question": question, "answer": answer}),
        success: home,
        error: errorHandler
    });
}

function errorHandler(e) {
    alert(`error: ${e.status} ${e.statusText} ${e.responseText}`);
}

function home() {
    window.location.replace(homeURL);
}

function closeModalCategory() {
    currentCategoryId = 0;
    $('#add_category_name').val("");
    $('#modalAddCategory').hide();
}

function closeModalCard() {
    currentCategoryId = 0;
    currentCardId = 0;
    $('#add_card_question').val("");
    $('#add_card_answer').val("");
    $('#modalAddCard').hide();
}
