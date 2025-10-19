// Global variables
let stompClient = null;
let currentUser = null;
let currentRoom = null;
let rooms = [];

// API Base URL
const API_BASE = 'http://localhost:8080/api';

// Initialize the application
document.addEventListener('DOMContentLoaded', function () {
    // Handle video background as a proper background component
    const video = document.getElementById('bg-video');
    if (video) {
        // Set video properties to prevent tab switching issues
        video.muted = true; // Start muted to avoid autoplay restrictions
        video.loop = true;
        video.playsInline = true;
        video.preload = 'auto';

        // Play the video immediately (muted)
        video.play().catch(() => {
            console.log('Video autoplay blocked, will play on user interaction');
        });

        // Unmute on first user interaction
        document.addEventListener('click', function unmute() {
            if (video.muted) {
                video.muted = false;
                updateMuteButton(false);
                document.removeEventListener('click', unmute);
            }
        }, { once: true });

        // Handle visibility change to prevent tab switching issues
        document.addEventListener('visibilitychange', function () {
            if (document.hidden) {
                // Tab is hidden, pause video
                video.pause();
            } else {
                // Tab is visible, resume video
                video.play().catch(() => {
                    // If play fails, try muted
                    video.muted = true;
                    video.play();
                });
            }
        });
    }

    // Check if user is already logged in
    const token = localStorage.getItem('token');
    if (token) {
        currentUser = JSON.parse(localStorage.getItem('user'));
        showChatSection();
        loadRooms();
    }

    // Close auth forms when clicking outside
    document.addEventListener('click', function (event) {
        const authForm = event.target.closest('.auth-form');
        const bottomAuth = event.target.closest('.bottom-auth');
        const muteBtn = event.target.closest('.mute-btn');

        if (!authForm && !bottomAuth && !muteBtn) {
            hideAuthForm();
        }
    });
});

// Mute Button Functions
function toggleMute() {
    const video = document.getElementById('bg-video');
    const muteBtn = document.getElementById('mute-btn');

    if (video) {
        video.muted = !video.muted;
        updateMuteButton(video.muted);
    }
}

function updateMuteButton(isMuted) {
    const muteBtn = document.getElementById('mute-btn');

    if (isMuted) {
        muteBtn.classList.add('muted');
    } else {
        muteBtn.classList.remove('muted');
    }
}

// Authentication Functions
function showAuthForm(formType) {
    // Hide all forms first
    document.getElementById('login-form').style.display = 'none';
    document.getElementById('register-form').style.display = 'none';

    // Hide bottom auth buttons
    document.querySelector('.bottom-auth').style.display = 'none';

    // Show selected form
    if (formType === 'login') {
        document.getElementById('login-form').style.display = 'flex';
    } else if (formType === 'register') {
        document.getElementById('register-form').style.display = 'flex';
    }
}

function hideAuthForm() {
    // Hide all forms
    document.getElementById('login-form').style.display = 'none';
    document.getElementById('register-form').style.display = 'none';

    // Show bottom auth buttons
    document.querySelector('.bottom-auth').style.display = 'flex';
}

// Password toggle function
function togglePassword(inputId) {
    const input = document.getElementById(inputId);
    const toggle = input.parentNode.querySelector('.password-toggle i');

    if (input.type === 'password') {
        input.type = 'text';
        toggle.classList.remove('fa-eye');
        toggle.classList.add('fa-eye-slash');
    } else {
        input.type = 'password';
        toggle.classList.remove('fa-eye-slash');
        toggle.classList.add('fa-eye');
    }
}

async function register() {
    const username = document.getElementById('register-username').value;
    const email = document.getElementById('register-email').value;
    const password = document.getElementById('register-password').value;
    const confirmPassword = document.getElementById('register-confirm-password').value;

    if (!username || !email || !password || !confirmPassword) {
        alert('Please fill in all fields');
        return;
    }

    if (password !== confirmPassword) {
        alert('Passwords do not match');
        return;
    }

    try {
        const response = await fetch(`${API_BASE}/auth/register`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ username, email, password })
        });

        const data = await response.json();

        if (response.ok) {
            alert('Registration successful! Please login.');
            showTab('login');
            clearForm('register');
        } else {
            alert(data.error || 'Registration failed');
        }
    } catch (error) {
        alert('Registration failed: ' + error.message);
    }
}

async function login() {
    const username = document.getElementById('login-username').value;
    const password = document.getElementById('login-password').value;

    if (!username || !password) {
        alert('Please fill in all fields');
        return;
    }

    try {
        const response = await fetch(`${API_BASE}/auth/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ username, password })
        });

        const data = await response.json();

        if (response.ok) {
            localStorage.setItem('token', data.accessToken);
            currentUser = {
                username: data.username,
                email: data.email,
                createdAt: data.createdAt
            };
            localStorage.setItem('user', JSON.stringify(currentUser));

            showChatSection();
            loadRooms();
        } else {
            alert(data.error || 'Login failed');
        }
    } catch (error) {
        alert('Login failed: ' + error.message);
    }
}

function logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    currentUser = null;
    currentRoom = null;

    if (stompClient) {
        stompClient.disconnect();
        stompClient = null;
    }

    showAuthSection();
}

function clearForm(formType) {
    if (formType === 'login') {
        document.getElementById('login-username').value = '';
        document.getElementById('login-password').value = '';
    } else {
        document.getElementById('register-username').value = '';
        document.getElementById('register-email').value = '';
        document.getElementById('register-password').value = '';
        document.getElementById('register-confirm-password').value = '';
    }
}

// UI Functions
function showAuthSection() {
    document.getElementById('auth-section').style.display = 'flex';
    document.getElementById('chat-section').style.display = 'none';
}

function showChatSection() {
    console.log('showChatSection called');
    console.log('currentUser:', currentUser);

    document.getElementById('auth-section').style.display = 'none';
    document.getElementById('chat-section').style.display = 'flex';

    const currentUserElement = document.getElementById('current-user');
    console.log('currentUserElement:', currentUserElement);

    if (currentUser && currentUser.username) {
        currentUserElement.textContent = currentUser.username;
        console.log('Set username to:', currentUser.username);
    } else {
        console.log('No currentUser or username found');
        currentUserElement.textContent = 'User';
    }
}

// Room Functions
async function loadRooms() {
    try {
        // Load public rooms
        const publicResponse = await fetch(`${API_BASE}/rooms`, {
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('token')}`
            }
        });

        if (publicResponse.ok) {
            const publicRooms = await publicResponse.json();
            displayRooms(publicRooms, 'public-rooms');
        }

        // Load user's rooms
        const myResponse = await fetch(`${API_BASE}/rooms/my`, {
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('token')}`
            }
        });

        if (myResponse.ok) {
            const myRooms = await myResponse.json();
            displayRooms(myRooms, 'my-rooms');
        }

        rooms = [...(await publicResponse.json()), ...(await myResponse.json())];
    } catch (error) {
        console.error('Error loading rooms:', error);
    }
}

function displayRooms(rooms, containerId) {
    const container = document.getElementById(containerId);
    container.innerHTML = '';

    rooms.forEach(room => {
        const roomElement = document.createElement('div');
        roomElement.className = 'room-item';
        roomElement.onclick = () => selectRoom(room);

        roomElement.innerHTML = `
            <h4>${room.name}</h4>
            <p>${room.description || 'No description'}</p>
            <small>${room.messageCount || 0} messages</small>
        `;

        container.appendChild(roomElement);
    });
}

async function selectRoom(room) {
    try {
        // Join the room
        const response = await fetch(`${API_BASE}/rooms/${room.id}/join`, {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('token')}`
            }
        });

        if (response.ok) {
            currentRoom = room;

            // Update UI
            document.getElementById('no-room-selected').style.display = 'none';
            document.getElementById('chat-room').style.display = 'flex';
            document.getElementById('room-name').textContent = room.name;

            // Update active room in sidebar
            document.querySelectorAll('.room-item').forEach(item => item.classList.remove('active'));
            event.target.closest('.room-item').classList.add('active');

            // Connect to WebSocket
            connectWebSocket();

            // Load messages
            loadMessages();

            // Load participants
            loadParticipants();
        } else {
            alert('Failed to join room');
        }
    } catch (error) {
        console.error('Error joining room:', error);
        alert('Failed to join room');
    }
}

async function leaveRoom() {
    if (!currentRoom) return;

    try {
        const response = await fetch(`${API_BASE}/rooms/${currentRoom.id}/leave`, {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('token')}`
            }
        });

        if (response.ok) {
            // Disconnect WebSocket
            if (stompClient) {
                stompClient.disconnect();
                stompClient = null;
            }

            // Reset UI
            currentRoom = null;
            document.getElementById('no-room-selected').style.display = 'flex';
            document.getElementById('chat-room').style.display = 'none';
            document.getElementById('messages').innerHTML = '';

            // Remove active class
            document.querySelectorAll('.room-item').forEach(item => item.classList.remove('active'));
        }
    } catch (error) {
        console.error('Error leaving room:', error);
    }
}

async function searchRooms() {
    const keyword = document.getElementById('room-search').value;
    if (!keyword.trim()) {
        loadRooms();
        return;
    }

    try {
        const response = await fetch(`${API_BASE}/rooms/search?keyword=${encodeURIComponent(keyword)}`, {
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('token')}`
            }
        });

        if (response.ok) {
            const searchResults = await response.json();
            displayRooms(searchResults, 'public-rooms');
        }
    } catch (error) {
        console.error('Error searching rooms:', error);
    }
}

// Modal Functions
function showRoomModal() {
    document.getElementById('room-modal').style.display = 'flex';
}

function closeRoomModal() {
    document.getElementById('room-modal').style.display = 'none';
    document.getElementById('new-room-name').value = '';
    document.getElementById('new-room-description').value = '';
    document.getElementById('new-room-private').checked = false;
    document.getElementById('new-room-max-participants').value = '';
}

async function createRoom() {
    const name = document.getElementById('new-room-name').value;
    const description = document.getElementById('new-room-description').value;
    const isPrivate = document.getElementById('new-room-private').checked;
    const maxParticipants = document.getElementById('new-room-max-participants').value;

    if (!name.trim()) {
        alert('Please enter a room name');
        return;
    }

    try {
        const roomData = {
            name: name,
            description: description,
            isPrivate: isPrivate
        };

        if (maxParticipants) {
            roomData.maxParticipants = parseInt(maxParticipants);
        }

        const response = await fetch(`${API_BASE}/rooms`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${localStorage.getItem('token')}`
            },
            body: JSON.stringify(roomData)
        });

        if (response.ok) {
            const data = await response.json();
            alert('Room created successfully!');
            closeRoomModal();
            loadRooms();
        } else {
            const error = await response.json();
            alert(error.error || 'Failed to create room');
        }
    } catch (error) {
        alert('Failed to create room: ' + error.message);
    }
}

// Message Functions
async function loadMessages() {
    if (!currentRoom) return;

    try {
        const response = await fetch(`${API_BASE}/messages/room/${currentRoom.id}?page=0&size=50`, {
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('token')}`
            }
        });

        if (response.ok) {
            const messages = await response.json();
            displayMessages(messages.reverse()); // Show oldest first
        }
    } catch (error) {
        console.error('Error loading messages:', error);
    }
}

function displayMessages(messages) {
    const messagesContainer = document.getElementById('messages');
    messagesContainer.innerHTML = '';

    messages.forEach(message => {
        const messageElement = createMessageElement(message);
        messagesContainer.appendChild(messageElement);
    });

    // Scroll to bottom
    messagesContainer.scrollTop = messagesContainer.scrollHeight;
}

function createMessageElement(message) {
    const messageDiv = document.createElement('div');
    const isOwnMessage = message.username === currentUser.username;
    const isSystemMessage = message.messageType === 'SYSTEM';

    messageDiv.className = `message ${isOwnMessage ? 'own' : ''} ${isSystemMessage ? 'system' : ''}`;

    const time = new Date(message.createdAt).toLocaleTimeString();

    messageDiv.innerHTML = `
        <div class="message-content">
            <div class="message-header">
                <span class="message-username">${message.username}</span>
                <span class="message-time">${time}</span>
            </div>
            <div class="message-text">${message.content}</div>
        </div>
    `;

    return messageDiv;
}

function handleMessageKeyPress(event) {
    if (event.key === 'Enter') {
        sendMessage();
    }
}

function sendMessage() {
    const messageInput = document.getElementById('message-input');
    const content = messageInput.value.trim();

    if (!content || !currentRoom) return;

    const message = {
        content: content,
        roomId: currentRoom.id
    };

    if (stompClient && stompClient.connected) {
        stompClient.send("/app/chat.sendMessage", {}, JSON.stringify(message));
        messageInput.value = '';
    }
}

async function loadParticipants() {
    if (!currentRoom) return;

    try {
        const response = await fetch(`${API_BASE}/rooms/${currentRoom.id}/participants`, {
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('token')}`
            }
        });

        if (response.ok) {
            const participants = await response.json();
            document.getElementById('room-participants').textContent =
                `${participants.length} participant${participants.length !== 1 ? 's' : ''}`;
        }
    } catch (error) {
        console.error('Error loading participants:', error);
    }
}

// WebSocket Functions
function connectWebSocket() {
    const socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);

    stompClient.connect({}, function (frame) {
        console.log('Connected to WebSocket');

        // Subscribe to room messages
        stompClient.subscribe(`/topic/room.${currentRoom.id}`, function (message) {
            const receivedMessage = JSON.parse(message.body);
            const messageElement = createMessageElement(receivedMessage);
            document.getElementById('messages').appendChild(messageElement);

            // Scroll to bottom
            const messagesContainer = document.getElementById('messages');
            messagesContainer.scrollTop = messagesContainer.scrollHeight;
        });

        // Send join message
        const joinMessage = {
            username: currentUser.username,
            roomId: currentRoom.id
        };
        stompClient.send("/app/chat.addUser", {}, JSON.stringify(joinMessage));
    }, function (error) {
        console.error('WebSocket connection error:', error);
    });
}

// Event listeners
window.addEventListener('beforeunload', function () {
    if (stompClient && stompClient.connected) {
        stompClient.send("/app/chat.leaveUser", {}, JSON.stringify({}));
    }
});

// User Profile Functions
function toggleUserProfile() {
    console.log('toggleUserProfile called');
    const menu = document.getElementById('user-profile-menu');
    const btn = document.getElementById('user-profile-btn');

    console.log('Menu element:', menu);
    console.log('Button element:', btn);
    console.log('Current user:', currentUser);

    if (menu.classList.contains('show')) {
        menu.classList.remove('show');
        btn.classList.remove('active');
        console.log('Menu closed');
    } else {
        menu.classList.add('show');
        btn.classList.add('active');
        console.log('Menu opened');

        // Populate profile info
        if (currentUser) {
            document.getElementById('profile-username').textContent = currentUser.username;
            document.getElementById('profile-email').textContent = currentUser.email || 'No email provided';
        }

        // Debug menu position
        const rect = menu.getBoundingClientRect();
        console.log('Menu position:', rect);
        console.log('Menu computed style:', window.getComputedStyle(menu));
    }
}

function showProfileSettings() {
    // Close the menu first
    toggleUserProfile();
    alert('Profile settings coming soon!');
}

function showProfileInfo() {
    // Close the menu first
    toggleUserProfile();
    alert(`Username: ${currentUser.username}\nEmail: ${currentUser.email || 'No email provided'}\nMember since: ${new Date(currentUser.createdAt).toLocaleDateString()}`);
}

// Close profile menu when clicking outside
document.addEventListener('click', function (event) {
    const menu = document.getElementById('user-profile-menu');
    const btn = document.getElementById('user-profile-btn');

    if (menu && !menu.contains(event.target) && !btn.contains(event.target)) {
        menu.classList.remove('show');
        btn.classList.remove('active');
    }
}); 