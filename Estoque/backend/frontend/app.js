const API = 'http://localhost:8080/api';
let token = null;

async function api(path, opts = {}) {
  const headers = Object.assign({}, opts.headers || {});
  const t = token || localStorage.getItem('token');
  if (t) headers['Authorization'] = 'Bearer ' + t;
  const res = await fetch(API + path, Object.assign({}, opts, { headers }));
  if (res.status === 204) return null;
  if (!res.ok) {
    const txt = await res.text();
    throw new Error('API error ' + res.status + ' ' + txt);
  }
  return res.json();
}

// ---------- Login page behavior ----------
function setupLoginPage() {
  // If current page is dashboard and token missing, redirect to index (login)
  const path = window.location.pathname.split('/').pop();
  // Elements might be on index.html (we merged login into index)
  const btnLogin = document.getElementById('btnLogin');
  const msg = document.getElementById('msg');
  if (!btnLogin) return;
  btnLogin.addEventListener('click', async () => {
    const emailEl = document.getElementById('email');
    const senhaEl = document.getElementById('senha');
    const email = emailEl ? emailEl.value : '';
    const senha = senhaEl ? senhaEl.value : '';
    try {
      const data = await api('/auth/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, senha, password: senha })
      });
      const tk = data.token || null;
      if (!tk) throw new Error('token missing in response');
      localStorage.setItem('token', tk);
      // Redirect to dashboard immediately
      window.location.href = 'dashboard.html';
    } catch (e) {
      if (msg) msg.textContent = 'Erro ao logar: ' + e.message;
      else alert('Erro ao logar: ' + e.message);
    }
  });
}

function setupDashboardPage() {
  if (!requireAuthOrRedirect()) return;

  const btnLogout = document.getElementById('btnLogout');
  if (btnLogout) {
    btnLogout.addEventListener('click', () => {
      localStorage.removeItem('token');
      token = null;
      window.location.href = 'login.html';
    });
  }

  setupEventHandlers(); // from events code
  setupEstoqueHandlers();
  // load data
  loadEventos();
  loadEstoque();
}

// --------- Estoque functions ----------
async function loadEstoque() {
  try {
    const items = await api('/estoque');
    const ul = document.getElementById('listaEstoque');
    if (!ul) return;
    ul.innerHTML = '';
    items.forEach(it => {
      const li = document.createElement('li');
      li.textContent = it.nome + ' — ' + (it.quantidade || 0) + ' — ' + (it.descricao || '');
      const btnDel = document.createElement('button');
      btnDel.textContent = 'Remover';
      btnDel.addEventListener('click', async () => {
        await api('/estoque/' + it.id, { method: 'DELETE' });
        loadEstoque();
      });
      li.appendChild(document.createTextNode(' '));
      li.appendChild(btnDel);
      ul.appendChild(li);
    });
  } catch (e) { console.error('Erro estoque:', e); }
}

function setupEstoqueHandlers() {
  const btnCreateItem = document.getElementById('btnCreateItem');
  if (!btnCreateItem) return;
  btnCreateItem.addEventListener('click', async () => {
    const nome = document.getElementById('item_nome').value;
    const quantidade = parseInt(document.getElementById('item_quantidade').value || '0', 10);
    const descricao = document.getElementById('item_descricao').value;
    try {
      await api('/estoque', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ nome, quantidade, descricao })
      });
      document.getElementById('item_nome').value = '';
      document.getElementById('item_quantidade').value = '';
      document.getElementById('item_descricao').value = '';
      loadEstoque();
    } catch (e) { alert('Erro criar item: ' + e.message); }
  });
}

// --------- Event functions (from previous app.js) ----------


function setupEventHandlers() {
  const btnLogin = document.getElementById('btnLogin');
  const btnLogout = document.getElementById('btnLogout');
  const btnCreate = document.getElementById('btnCreate');

  if (btnLogin) {
    btnLogin.addEventListener('click', async () => {
      const email = document.getElementById('email').value;
      const senha = document.getElementById('senha').value;
      try {
        const data = await api('/auth/login', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ email, senha, password: senha }) // send fallback 'password' too
        });
        token = data.token || null;
        const loginBox = document.getElementById('loginBox');
        const appBox = document.getElementById('appBox');
        if (loginBox) loginBox.style.display = 'none';
        if (appBox) appBox.style.display = 'block';
        loadEventos();
      } catch (e) {
        alert('Erro ao logar: ' + e.message);
      }
    });
  }

  if (btnLogout) {
    btnLogout.addEventListener('click', () => {
      token = null;
      const loginBox = document.getElementById('loginBox');
      const appBox = document.getElementById('appBox');
      if (loginBox) loginBox.style.display = 'block';
      if (appBox) appBox.style.display = 'none';
    });
  }

  if (btnCreate) {
    btnCreate.addEventListener('click', async () => {
      const titulo = document.getElementById('titulo').value;
      const descricao = document.getElementById('descricao').value;
      const data_evento = document.getElementById('data_evento').value;
      try {
        await api('/eventos', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ titulo, descricao, dataEvento: data_evento })
        });
        // clear form
        const tituloEl = document.getElementById('titulo');
        const descricaoEl = document.getElementById('descricao');
        const dataEl = document.getElementById('data_evento');
        if (tituloEl) tituloEl.value = '';
        if (descricaoEl) descricaoEl.value = '';
        if (dataEl) dataEl.value = '';
        loadEventos();
      } catch (e) { alert('Erro criar evento: ' + e.message); }
    });
  }
}

async function loadEventos() {
  try {
    const eventos = await api('/eventos');
    const ul = document.getElementById('lista');
    if (!ul) return;
    ul.innerHTML = '';
    eventos.forEach(ev => {
      const li = document.createElement('li');
      li.textContent = ev.titulo + ' - ' + (ev.dataEvento || '');
      ul.appendChild(li);
    });
  } catch (e) { alert('Erro ao carregar eventos: ' + e.message); }
}

// --------- Estoque functions ----------
async function loadEstoque() {
  try {
    const items = await api('/estoque');
    const ul = document.getElementById('listaEstoque');
    if (!ul) return;
    ul.innerHTML = '';
    items.forEach(it => {
      const li = document.createElement('li');
      li.textContent = it.nome + ' — ' + (it.quantidade || 0) + ' — ' + (it.descricao || '');
      const btnDel = document.createElement('button');
      btnDel.textContent = 'Remover';
      btnDel.addEventListener('click', async () => {
        await api('/estoque/' + it.id, { method: 'DELETE' });
        loadEstoque();
      });
      li.appendChild(document.createTextNode(' '));
      li.appendChild(btnDel);
      ul.appendChild(li);
    });
  } catch (e) { console.error('Erro estoque:', e); }
}

function setupEstoqueHandlers() {
  const btnCreateItem = document.getElementById('btnCreateItem');
  if (!btnCreateItem) return;
  btnCreateItem.addEventListener('click', async () => {
    const nome = document.getElementById('item_nome').value;
    const quantidade = parseInt(document.getElementById('item_quantidade').value || '0', 10);
    const descricao = document.getElementById('item_descricao').value;
    try {
      await api('/estoque', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ nome, quantidade, descricao })
      });
      document.getElementById('item_nome').value = '';
      document.getElementById('item_quantidade').value = '';
      document.getElementById('item_descricao').value = '';
      loadEstoque();
    } catch (e) { alert('Erro criar item: ' + e.message); }
  });
}

// initial UI state and safe setup after DOM is ready
document.addEventListener('DOMContentLoaded', () => {
  const loginBox = document.getElementById('loginBox');
  const appBox = document.getElementById('appBox');
  if (loginBox) loginBox.style.display = 'block';
  if (appBox) appBox.style.display = 'none';
  setupEventHandlers();
  setupEstoqueHandlers();
  loadEstoque();
});