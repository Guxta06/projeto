
const API = 'http://localhost:8080/api';

function debugLog(...args) {
  try { console.log(...args); } catch (e) {}
}

async function api(path, opts = {}) {
  const headers = { ...(opts.headers || {}) };

  const token = localStorage.getItem("token");
  if (token) headers["Authorization"] = "Bearer " + token;

  const res = await fetch(API + path, { ...opts, headers });

  let raw = null;
  try { raw = await res.clone().text(); } catch {}

  if (res.status === 204) return null;

  if (!res.ok) {
    const err = new Error("API error " + res.status + " " + (raw || ""));
    err.status = res.status;
    err.raw = raw;
    throw err;
  }

  try {
    return await res.json();
  } catch {
    return raw;
  }
}


// LOGIN PÁGINA

function setupLoginPage() {
  const btn = document.getElementById("btnLogin");
  if (!btn) return; // não está na página de login

  btn.addEventListener("click", async () => {
    const email = document.getElementById("email")?.value || "";
    const senha = document.getElementById("senha")?.value || "";
    const msg = document.getElementById("msg");

    msg.textContent = "";

    if (!email || !senha) {
      msg.textContent = "Preencha email e senha.";
      return;
    }

    try {
      const res = await fetch(API + "/auth/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email, senha })
      });

      const text = await res.text();
      debugLog("Login response:", text);

      let data;
      try { data = JSON.parse(text); }
      catch { data = {}; }

      const token =
        data.token ||
        data.accessToken ||
        data.access_token ||
        data.jwt ||
        null;

      if (!token) {
        msg.textContent = "Token ausente na resposta do servidor.";
        console.error("Resposta sem token:", data);
        return;
      }

      localStorage.setItem("token", token);
      window.location.href = "dashboard.html";

    } catch (e) {
      msg.textContent = "Erro ao logar: " + e.message;
      console.error(e);
    }
  });
}



// AUTH CHECK (Dashboard)


async function requireAuthOrRedirect() {
  const token = localStorage.getItem("token");
  if (!token) {
    window.location.href = "index.html";
    return false;
  }

  try {
    const res = await fetch(API + "/estoque", {
      headers: { Authorization: "Bearer " + token }
    });

    if (res.status === 200) return true;

    if (res.status === 401) {
      localStorage.removeItem("token");
      window.location.href = "index.html";
      return false;
    }

    return true;

  } catch (e) {
    console.error("Erro validando token:", e);
    localStorage.removeItem("token");
    window.location.href = "index.html";
    return false;
  }
}



// DASHBOARD PÁGINA


function setupDashboardPage() {
  const btnLogout = document.getElementById("btnLogout");
  if (btnLogout) {
    btnLogout.addEventListener("click", () => {
      localStorage.removeItem("token");
      window.location.href = "index.html";
    });
  }

  setupEstoqueHandlers();
  loadEstoque();
}



// ESTOQUE - LISTAR

async function loadEstoque() {
  const ul = document.getElementById("listaEstoque");
  if (!ul) return;

  ul.innerHTML = "Carregando...";

  try {
    const items = await api("/estoque");

    if (!Array.isArray(items)) {
      ul.innerHTML = "Resposta inesperada (ver console)";
      console.error("Resposta estoque:", items);
      return;
    }

    ul.innerHTML = "";

    items.forEach((it) => {
      const li = document.createElement("li");
      li.textContent =
        (it.nome || "(nome)") +
        " — " +
        (it.quantidade || 0) +
        " — " +
        (it.descricao || "");

      const btnDel = document.createElement("button");
      btnDel.textContent = "Remover";
      btnDel.style.marginLeft = "8px";

      btnDel.addEventListener("click", async () => {
        try {
          if (!it.id) {
            alert("ID inexistente no item. Verifique o backend.");
            console.error("Item sem id:", it);
            return;
          }

          console.log("DELETE /estoque/" + it.id);

          const res = await fetch(API + "/estoque/" + it.id, {
            method: "DELETE",
            headers: { Authorization: "Bearer " + localStorage.getItem("token") }
          });

          console.log("DELETE status:", res.status);

          if (res.status === 204 || res.status === 200) {
            loadEstoque();
            return;
          }

          if (res.status === 401) {
            alert("Não autorizado. Faça login novamente.");
            localStorage.removeItem("token");
            window.location.href = "index.html";
            return;
          }

          const txt = await res.text();
          alert("Erro ao remover: " + res.status + " - " + txt);

        } catch (e) {
          alert("Erro ao remover item (ver console)");
          console.error(e);
        }
      });

      li.appendChild(btnDel);
      ul.appendChild(li);
    });

  } catch (e) {
    ul.innerHTML = "Erro ao carregar estoque.";
    console.error(e);
  }
}



// ESTOQUE - CRIAR

function setupEstoqueHandlers() {
  const btn = document.getElementById("btnCreateItem");
  if (!btn) return;

  btn.addEventListener("click", async () => {
    const nome = document.getElementById("item_nome")?.value || "";
    const quantidade = parseInt(document.getElementById("item_quantidade")?.value || "0", 10);
    const descricao = document.getElementById("item_descricao")?.value || "";

    if (!nome) {
      alert("Nome é obrigatório.");
      return;
    }

    try {
      await api("/estoque", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ nome, quantidade, descricao })
      });

      loadEstoque();

    } catch (e) {
      alert("Erro ao criar item (ver console)");
      console.error(e);
    }
  });
}



// AUTO INICIALIZAÇÃO POR PÁGINA

document.addEventListener("DOMContentLoaded", () => {
  const page = window.location.pathname.split("/").pop();

  if (page === "" || page === "index.html") {
    setupLoginPage();
    return;
  }

  if (page === "dashboard.html") {
    requireAuthOrRedirect().then((ok) => {
      if (ok) setupDashboardPage();
    });
    return;
  }
});
