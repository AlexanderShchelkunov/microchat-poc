let id = id => document.getElementById(id);

var ws = new WebSocket("ws://localhost:8080/chat");
ws.onmessage = message => updateChat(message);
ws.onclose = () => console.log("WebSocket connection closed");

id('login').addEventListener('click', () => {
  let username = id('username').value;
  if (username !== '') {
    sendUserJoinedEvent(username);
  }
});

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
      user: 'Username',
      payload: {
        text: message
      }
    };
    ws.send(JSON.stringify(event));
    id("message").value = "";
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
    return `<p> ${data.user}: ${data.payload.text}</p>`;
  }
  if (data.type === 'USER_JOINED') {
    return `<p>User ${data.user} joined</p>`;
  }
  if (data.type === 'USER_LEFT') {
    return `<p>User ${data.user} left</p>`;
  }
  console.log('Unknown event type', data);
}