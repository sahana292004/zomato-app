// ══════════════════════════════════════
//  Zomato — Frontend Application JS
// ══════════════════════════════════════

let currentUser = null;
let restaurants = [];
let filteredRestaurants = [];
let currentRestaurant = null;
let cart = {}; // { menuItemId: { id, name, price, qty, emoji, restId, restName } }
let selectedCategory = 'All';

// Order Tracking State
let trackingInterval = null;
let trackingStep = 0;
let currentTrackingOrderId = null;
const trackSteps = [
  { title: 'Order Confirmed', time: 'Just now' },
  { title: 'Preparing Food', time: '2 mins away' },
  { title: 'Out for Delivery', time: '10 mins away' },
  { title: 'Delivered', time: 'Arrived' }
];

// Initialize App
document.addEventListener("DOMContentLoaded", () => {
  checkSession();
});

// ── Session & Auth Checks ──
async function checkSession() {
  try {
    const res = await fetch('/api/me');
    if (res.ok) {
      currentUser = await res.json();
      showApp();
    } else {
      hideApp();
    }
  } catch (e) {
    hideApp();
  }
}

function showApp() {
  document.getElementById('auth-page').style.display = 'none';
  document.getElementById('app').style.display = 'flex';
  
  // Set Profile navbar / card details
  document.getElementById('prof-avatar').textContent = currentUser.name.charAt(0).toUpperCase();
  document.getElementById('prof-name').textContent = currentUser.name;
  document.getElementById('prof-email').textContent = currentUser.email;
  
  loadRestaurants();
  showScreen('home');
}

function hideApp() {
  document.getElementById('auth-page').style.display = 'flex';
  document.getElementById('app').style.display = 'none';
  switchAuth('login');
}

// ── Auth Handling ──
function switchAuth(mode) {
  document.getElementById('login-form').style.display = mode === 'login' ? 'block' : 'none';
  document.getElementById('register-form').style.display = mode === 'register' ? 'block' : 'none';
  document.getElementById('tab-login').classList.toggle('active', mode === 'login');
  document.getElementById('tab-register').classList.toggle('active', mode === 'register');
  document.getElementById('lerr').style.display = 'none';
  document.getElementById('rerr').style.display = 'none';
  document.getElementById('rok').style.display = 'none';
}

function togglePwd(id, btn) {
  const inp = document.getElementById(id);
  if (inp.type === 'password') {
    inp.type = 'text';
    btn.textContent = '🙈';
  } else {
    inp.type = 'password';
    btn.textContent = '👁️';
  }
}

async function doLogin(e) {
  e.preventDefault();
  const username = document.getElementById('lu').value.trim();
  const password = document.getElementById('lp').value;
  const errEl = document.getElementById('lerr');
  
  errEl.style.display = 'none';
  
  try {
    const res = await fetch('/api/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ username, password })
    });
    
    const data = await res.json();
    if (res.ok && data.success) {
      currentUser = data.user;
      toast('Welcome to Zomato!');
      showApp();
    } else {
      errEl.textContent = data.error || 'Invalid credentials';
      errEl.style.display = 'block';
    }
  } catch (err) {
    errEl.textContent = 'Connection failed';
    errEl.style.display = 'block';
  }
}

async function doRegister(e) {
  e.preventDefault();
  const name = document.getElementById('rname').value.trim();
  const username = document.getElementById('ru').value.trim();
  const email = document.getElementById('remail').value.trim();
  const password = document.getElementById('rp').value;
  const rp2 = document.getElementById('rp2').value;
  
  const errEl = document.getElementById('rerr');
  const okEl = document.getElementById('rok');
  errEl.style.display = 'none';
  okEl.style.display = 'none';
  
  if (password !== rp2) {
    errEl.textContent = 'Passwords do not match';
    errEl.style.display = 'block';
    return;
  }
  
  try {
    const res = await fetch('/api/register', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ name, username, email, password })
    });
    
    const data = await res.json();
    if (res.ok && data.success) {
      okEl.textContent = 'Account created! Please sign in.';
      okEl.style.display = 'block';
      setTimeout(() => switchAuth('login'), 1500);
    } else {
      errEl.textContent = data.error || 'Registration failed';
      errEl.style.display = 'block';
    }
  } catch (err) {
    errEl.textContent = 'Connection failed';
    errEl.style.display = 'block';
  }
}

async function doLogout() {
  try {
    const res = await fetch('/api/logout', { method: 'POST' });
    if (res.ok) {
      currentUser = null;
      cart = {};
      updateCartBadge();
      toast('Logged out successfully.');
      hideApp();
    }
  } catch (e) {
    toast('Logout failed.', false);
  }
}

// ── Screen Navigation ──
function showScreen(screenId) {
  document.querySelectorAll('.screen').forEach(s => s.classList.remove('active'));
  document.getElementById(`screen-${screenId}`).classList.add('active');
  
  if (screenId === 'home') {
    // Reset search
    document.getElementById('search-input').value = '';
    filterRestaurants('');
  } else if (screenId === 'cart') {
    renderCart();
  } else if (screenId === 'profile') {
    loadOrderHistory();
  }
}

function goHome() {
  showScreen('home');
}

// ── Toast Helper ──
function toast(msg, ok = true) {
  const t = document.getElementById('toast');
  t.textContent = (ok ? '✅ ' : '❌ ') + msg;
  t.className = 'toast show' + (ok ? '' : ' error');
  setTimeout(() => t.className = 'toast', 3000);
}

// ── Load & Filter Restaurants ──
async function loadRestaurants() {
  const container = document.getElementById('restaurant-list');
  container.innerHTML = '<div style="grid-column:1/-1;text-align:center;padding:40px;">⌛ Loading restaurants...</div>';
  
  try {
    const res = await fetch('/api/restaurants');
    if (res.ok) {
      restaurants = await res.json();
      filteredRestaurants = [...restaurants];
      renderRestaurants();
    } else {
      container.innerHTML = '<div style="grid-column:1/-1;text-align:center;padding:40px;color:red;">❌ Failed to load restaurants.</div>';
    }
  } catch (e) {
    container.innerHTML = '<div style="grid-column:1/-1;text-align:center;padding:40px;color:red;">❌ Connection error.</div>';
  }
}

function renderRestaurants() {
  const container = document.getElementById('restaurant-list');
  
  const displayList = filteredRestaurants.filter(r => selectedCategory === 'All' || r.category === selectedCategory);
  
  if (displayList.length === 0) {
    container.innerHTML = '<div style="grid-column:1/-1;text-align:center;padding:40px;color:#888;">No restaurants found.</div>';
    return;
  }
  
  container.innerHTML = displayList.map(r => `
    <div class="r-card" onclick="openRestaurant(${r.id})">
      <div class="r-img">${r.emoji}</div>
      <div class="r-body">
        <div class="r-name">${esc(r.name)}</div>
        <div class="r-meta">
          <span class="r-rating">${r.rating} ★</span>
          <span class="r-time">⏱️ ${r.deliveryTime}</span>
          <span class="r-price">₹${r.deliveryFee === 0 ? 'Free' : r.deliveryFee + ' delivery'}</span>
        </div>
        <div class="r-tags">
          ${r.tags.split(',').map(tag => `<span class="r-tag">${esc(tag.trim())}</span>`).join('')}
        </div>
      </div>
    </div>
  `).join('');
}

function filterCat(elem, catName) {
  document.querySelectorAll('.cat').forEach(c => c.classList.remove('active'));
  elem.classList.add('active');
  selectedCategory = catName;
  document.getElementById('rest-title').textContent = catName === 'All' ? 'Top Restaurants Near You' : `${catName} Specials`;
  renderRestaurants();
}

function filterRestaurants(query) {
  const q = query.toLowerCase().trim();
  filteredRestaurants = restaurants.filter(r => 
    r.name.toLowerCase().includes(q) || 
    r.tags.toLowerCase().includes(q)
  );
  renderRestaurants();
}

// ── Open Restaurant Details ──
async function openRestaurant(id) {
  try {
    const res = await fetch(`/api/restaurants/${id}`);
    if (res.ok) {
      currentRestaurant = await res.json();
      
      document.getElementById('rest-cover').textContent = currentRestaurant.emoji;
      document.getElementById('rest-detail-name').textContent = currentRestaurant.name;
      document.getElementById('rest-detail-tags').textContent = currentRestaurant.tags;
      document.getElementById('rest-detail-rating').textContent = `${currentRestaurant.rating} ★`;
      document.getElementById('rest-detail-time').textContent = currentRestaurant.deliveryTime;
      document.getElementById('rest-detail-delivery-fee').textContent = currentRestaurant.deliveryFee === 0 ? 'FREE' : `₹${currentRestaurant.deliveryFee}`;
      
      renderMenu();
      updateFloatingCartBar();
      showScreen('restaurant');
    }
  } catch (e) {
    toast('Error opening restaurant details', false);
  }
}

function renderMenu() {
  const container = document.getElementById('menu-list');
  if (!currentRestaurant.menuItems || currentRestaurant.menuItems.length === 0) {
    container.innerHTML = '<div style="text-align:center;padding:20px;color:#888;">No menu items listed.</div>';
    return;
  }
  
  // Group menu items by category
  const categories = {};
  currentRestaurant.menuItems.forEach(item => {
    if (!categories[item.category]) categories[item.category] = [];
    categories[item.category].push(item);
  });
  
  container.innerHTML = Object.keys(categories).map(cat => `
    <div class="menu-section-title">${esc(cat)}</div>
    ${categories[cat].map(item => {
      const cartItem = cart[item.id];
      const qty = cartItem ? cartItem.qty : 0;
      
      return `
        <div class="menu-item">
          <div class="menu-item-img">${item.emoji}</div>
          <div class="menu-item-info">
            <div>
              <span class="veg-dot ${item.isVeg ? 'veg' : 'nonveg'}"></span>
              <strong class="item-name">${esc(item.name)}</strong>
            </div>
            <div class="item-desc">${esc(item.description)}</div>
            <div class="item-price">₹${item.price}</div>
          </div>
          <div>
            ${qty > 0 ? `
              <div class="qty-ctrl">
                <button class="qty-btn" onclick="updateQty(${item.id}, -1)">−</button>
                <span class="qty-num">${qty}</span>
                <button class="qty-btn" onclick="updateQty(${item.id}, 1)">+</button>
              </div>
            ` : `
              <button class="add-btn" onclick="addToCart(${item.id})">ADD</button>
            `}
          </div>
        </div>
      `;
    }).join('')}
  `).join('');
}

// ── Cart Management ──
function addToCart(itemId) {
  if (!currentUser) {
    toast('Please log in to add items to your cart.', false);
    showScreen('profile');
    return;
  }

  const item = currentRestaurant.menuItems.find(mi => mi.id === itemId);
  if (!item) return;

  // Clear cart if items from a different restaurant are added
  const cartValues = Object.values(cart);
  if (cartValues.length > 0 && cartValues[0].restId !== currentRestaurant.id) {
    if (confirm(`You have items from ${cartValues[0].restName} in your cart. Discard cart and order from ${currentRestaurant.name}?`)) {
      cart = {};
    } else {
      return;
    }
  }

  cart[itemId] = {
    id: item.id,
    name: item.name,
    price: item.price,
    qty: 1,
    emoji: item.emoji,
    restId: currentRestaurant.id,
    restName: currentRestaurant.name
  };

  updateCartBadge();
  renderMenu();
  updateFloatingCartBar();
}

function updateQty(itemId, delta) {
  if (!cart[itemId]) return;
  cart[itemId].qty += delta;
  
  if (cart[itemId].qty <= 0) {
    delete cart[itemId];
  }
  
  updateCartBadge();
  renderMenu();
  updateFloatingCartBar();
}

function updateCartBadge() {
  const totalQty = Object.values(cart).reduce((sum, item) => sum + item.qty, 0);
  document.getElementById('cart-badge').textContent = totalQty;
}

function updateFloatingCartBar() {
  const bar = document.getElementById('floating-cart-bar');
  const items = Object.values(cart);
  if (items.length > 0 && currentRestaurant && items[0].restId === currentRestaurant.id) {
    const qty = items.reduce((sum, item) => sum + item.qty, 0);
    const total = items.reduce((sum, item) => sum + (item.qty * item.price), 0);
    
    document.getElementById('fc-count').textContent = qty;
    document.getElementById('fc-rest').textContent = currentRestaurant.name;
    document.getElementById('fc-total').textContent = total;
    bar.style.display = 'flex';
  } else {
    bar.style.display = 'none';
  }
}

function renderCart() {
  const container = document.getElementById('cart-content');
  const items = Object.values(cart);
  
  if (items.length === 0) {
    container.innerHTML = `
      <div class="empty-cart">
        <div style="font-size:60px;margin-bottom:12px;">🛒</div>
        <p>Your cart is empty.</p>
        <button class="btn btn-primary" style="margin-top:16px;" onclick="goHome()">Browse Restaurants</button>
      </div>
    `;
    return;
  }
  
  const subtotal = items.reduce((sum, item) => sum + (item.qty * item.price), 0);
  const deliveryFee = subtotal > 499 ? 0 : items[0].price > 0 ? 30 : 0; // standard fee
  const taxes = Math.round(subtotal * 0.05);
  const total = subtotal + deliveryFee + taxes;
  
  container.innerHTML = `
    <div style="font-size: 13px; color: var(--text-muted); margin-bottom: 12px;">
      Ordering from <strong>${esc(items[0].restName)}</strong>
    </div>
    <div style="margin-bottom:20px;">
      ${items.map(item => `
        <div class="cart-item">
          <div class="cart-item-emoji">${item.emoji}</div>
          <div class="cart-item-name">${esc(item.name)}</div>
          <div class="qty-ctrl">
            <button class="qty-btn" onclick="updateCartItemQty(${item.id}, -1)">−</button>
            <span class="qty-num">${item.qty}</span>
            <button class="qty-btn" onclick="updateCartItemQty(${item.id}, 1)">+</button>
          </div>
          <div class="cart-item-price">₹${item.qty * item.price}</div>
        </div>
      `).join('')}
    </div>
    
    <div class="bill">
      <h3>BILL SUMMARY</h3>
      <div class="bill-row">
        <span>Item Total</span>
        <span>₹${subtotal}</span>
      </div>
      <div class="bill-row">
        <span>Delivery Partner Fee</span>
        <span>${deliveryFee === 0 ? '<span style="color:#4caf50;">FREE</span>' : '₹' + deliveryFee}</span>
      </div>
      <div class="bill-row">
        <span>Taxes & Restaurant Charges (5%)</span>
        <span>₹${taxes}</span>
      </div>
      <div class="bill-row total">
        <span>Grand Total</span>
        <span>₹${total}</span>
      </div>
    </div>
    
    <button class="checkout-btn" onclick="placeOrder()">Place Order & Pay · ₹${total}</button>
  `;
}

function updateCartItemQty(itemId, delta) {
  if (!cart[itemId]) return;
  cart[itemId].qty += delta;
  if (cart[itemId].qty <= 0) {
    delete cart[itemId];
  }
  updateCartBadge();
  renderCart();
}

// ── Place Order & Track ──
async function placeOrder() {
  const items = Object.values(cart);
  if (items.length === 0) return;
  
  const payload = {
    restaurantName: items[0].restName,
    items: items.map(item => ({
      name: item.name,
      price: item.price,
      quantity: item.qty
    }))
  };
  
  try {
    const res = await fetch('/api/orders', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload)
    });
    
    const data = await res.json();
    if (res.ok && data.success) {
      currentTrackingOrderId = data.orderId;
      cart = {};
      updateCartBadge();
      
      document.getElementById('order-id').textContent = `Order #ZMT-${currentTrackingOrderId}`;
      showScreen('tracking');
      
      trackingStep = 0;
      renderTracking();
      startTrackingSimulation();
    } else {
      toast(data.error || 'Failed to place order', false);
    }
  } catch (e) {
    toast('Checkout connection error', false);
  }
}

function renderTracking() {
  const container = document.getElementById('track-steps');
  
  container.innerHTML = trackSteps.map((step, idx) => {
    let status = 'pending';
    let icon = '';
    
    if (idx < trackingStep) {
      status = 'done';
      icon = '✓';
    } else if (idx === trackingStep) {
      status = 'active';
      icon = '⏳';
    }
    
    return `
      <div class="track-step">
        <div class="step-line"></div>
        <div class="step-dot ${status}">${icon}</div>
        <div class="step-info">
          <div class="step-title ${status === 'pending' ? 'pending' : ''}">${step.title}</div>
          <div class="step-time">${status !== 'pending' ? step.time : 'Upcoming'}</div>
        </div>
      </div>
    `;
  }).join('');
}

function startTrackingSimulation() {
  if (trackingInterval) clearInterval(trackingInterval);
  
  // Every 4 seconds, advance status and notify backend
  trackingInterval = setInterval(async () => {
    if (trackingStep < trackSteps.length - 1) {
      trackingStep++;
      renderTracking();
      
      // Update backend
      const nextStatus = trackSteps[trackingStep].title;
      try {
        await fetch(`/api/orders/${currentTrackingOrderId}/status`, {
          method: 'PUT',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ status: nextStatus })
        });
      } catch(e) {}
    } else {
      clearInterval(trackingInterval);
      toast('Your food has been delivered! Enjoy your meal!');
    }
  }, 4000);
}

// ── Profile / Past Orders ──
async function loadOrderHistory() {
  const container = document.getElementById('order-history-list');
  container.innerHTML = '<div style="text-align:center;padding:20px;color:#888;">Loading history...</div>';
  
  try {
    const res = await fetch('/api/orders');
    if (res.ok) {
      const orders = await res.json();
      renderOrderHistory(orders);
    } else {
      container.innerHTML = '<div style="text-align:center;padding:20px;color:red;">Failed to load order history.</div>';
    }
  } catch (e) {
    container.innerHTML = '<div style="text-align:center;padding:20px;color:red;">Connection error.</div>';
  }
}

function renderOrderHistory(orders) {
  const container = document.getElementById('order-history-list');
  if (orders.length === 0) {
    container.innerHTML = '<div style="text-align:center;padding:20px;color:#888;">No orders placed yet.</div>';
    return;
  }
  
  container.innerHTML = orders.map(o => {
    const dateStr = new Date(o.createdAt).toLocaleDateString('en-IN', {
      day: 'numeric', month: 'short', year: 'numeric', hour: '2-digit', minute: '2-digit'
    });
    
    return `
      <div class="history-card">
        <div class="history-header">
          <span>${esc(o.restaurantName)}</span>
          <span style="color:var(--primary);">${esc(o.status)}</span>
        </div>
        <div class="history-body">
          <div style="font-weight:600;margin-bottom:4px;">Items Ordered:</div>
          ${o.items.map(item => `
            <div style="display:flex;justify-content:space-between;font-size:13px;color:#ccc;margin-bottom:2px;">
              <span>${esc(item.name)} (x${item.quantity})</span>
              <span>₹${item.price * item.quantity}</span>
            </div>
          `).join('')}
        </div>
        <div class="history-footer">
          <span>📅 ${dateStr}</span>
          <strong>Paid: ₹${o.total}</strong>
        </div>
      </div>
    `;
  }).join('');
}

// ── Escape String Helpers to Prevent XSS Injection ──
function esc(s) {
  return String(s)
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#039;');
}

function escJS(s) {
  return String(s).replace(/'/g, "\\'");
}
