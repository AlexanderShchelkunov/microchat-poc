let id = id => document.getElementById(id);

var ws = new WebSocket("ws://localhost:8080/chat");
ws.onmessage = message => updateChat(message);
ws.onclose = () => console.log("WebSocket connection closed");
var login = 'Username';

id('login').addEventListener('click', onLogin);

id("username").addEventListener("keypress", function (e) {
  if (e.keyCode === 13 && id('username').value) {
    onLogin();
  }
});

function onLogin() {
  let username = id('username').value;
  if (username !== '') {
    sendUserJoinedEvent(username);
    login = username;
    id("loginPane").style.display = 'none';
    id("chat-block").style.display = 'block';
    id("welcome").innerHTML = id("welcome").innerHTML.replace("login", login);
    id("message").focus();
  }
}

function sendUserJoinedEvent(name) {
  var event = userJoinedEvent(name);
  ws.send(JSON.stringify(event));
}

function userJoinedEvent(name) {
  return {
    type: 'USER_JOINED',
    user: name
  };
}

id("send").addEventListener("click", () => sendAndClear(id("message").value));
id("message").addEventListener("keypress", function (e) {
  if (e.keyCode === 13) { // Send message if enter is pressed in input field
    sendAndClear(e.target.value);
  }
});

var sendAndClear = (message) => {
  if (message !== "") {
    var event = {
      type: 'CHAT_MESSAGE',
      user: login,
      payload: {
        text: message
      }
    };
    ws.send(JSON.stringify(event));
    id("message").value = "";
    id("message").focus();
  }
};

function updateChat(event) {
  var message = toMessage(event);
  id("chat").insertAdjacentHTML("afterbegin", message);
  // id("userlist").innerHTML = data.payload.users.map(user => `<li>${user}</li>`).join(""); TODO
}

function toMessage(event) {
  var data = JSON.parse(event.data);
  if (data.type === 'CHAT_MESSAGE') {
    return `<div class="message"> <div class="user-login">${data.user}</div> <div>${data.payload.text}</div> </div>`;
  }
  if (data.type === 'USER_JOINED') {
    return `<div class="message system-message">User ${data.user} just joined the chat</div>`;
  }
  if (data.type === 'USER_LEFT') {
    return `<div class="message system-message">User ${data.user} just left the chat</div>`;
  }
  console.log('Unknown event type', data);
}

id("username").focus();