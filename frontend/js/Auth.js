let authMode = 'login';

function switchTab(mode) {
  authMode = mode;
  document.getElementById('login-tab').classList.toggle('active', mode === 'login');
  document.getElementById('register-tab').classList.toggle('active', mode === 'register');
  document.getElementById('name-wrap').style.display = mode === 'register' ? 'flex' : 'none';
  document.getElementById('auth-btn').textContent = mode === 'login' ? 'Sign In' : 'Create Account';
  document.getElementById('auth-msg').textContent = '';
}

async function submitAuth() {
  const email    = document.getElementById('auth-email').value.trim();
  const password = document.getElementById('auth-password').value.trim();
  const msg      = document.getElementById('auth-msg');

  if (!email || !password) { msg.textContent = 'Please fill all fields.'; return; }

  let body = { email, password };
  let url  = `${API}/auth/login`;

  if (authMode === 'register') {
    const name = document.getElementById('auth-name').value.trim();
    if (!name) { msg.textContent = 'Please enter your name.'; return; }
    body = { name, email, password };
    url  = `${API}/auth/register`;
  }

  try {
    const res  = await fetch(url, { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(body) });
    const data = await res.json();
    if (!res.ok) { msg.textContent = data.error || 'Request failed'; return; }

    currentUser = data;
    document.getElementById('auth-screen').style.display = 'none';
    document.getElementById('app').classList.add('visible');
    document.getElementById('topbar-user-name').textContent = currentUser.name;

    if (currentUser.role === 'ADMIN') {
      document.getElementById('nav-admin').style.display = 'flex';
    }

    showPage('hotels');
  } catch {
    msg.textContent = 'Cannot reach server. Is the backend running?';
  }
}

function logout() {
  currentUser = null;
  document.getElementById('app').classList.remove('visible');
  document.getElementById('auth-screen').style.display = 'flex';
  document.getElementById('auth-password').value = '';
  document.getElementById('auth-msg').textContent = '';
  document.getElementById('nav-admin').style.display = 'none';
}

document.getElementById('auth-password').addEventListener('keydown', e => {
  if (e.key === 'Enter') submitAuth();
});