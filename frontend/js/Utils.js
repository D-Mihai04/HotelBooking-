const API = 'http://localhost:8080/api';

let currentUser    = null;
let pendingBooking = null;

function dateStr(offsetDays) {
  const d = new Date();
  d.setDate(d.getDate() + offsetDays);
  return d.toISOString().split('T')[0];
}

function setStatus(msg, ok) {
  const bar = document.getElementById('status-bar');
  bar.textContent = msg;
  bar.style.color = ok ? 'var(--success)' : 'var(--error)';
}

function showPage(page) {
  ['hotels', 'mybookings', 'admin'].forEach(p =>
    document.getElementById('nav-' + p)?.classList.toggle('active', p === page)
  );
  const main = document.getElementById('main-content');
  if      (page === 'hotels')     renderHotels(main);
  else if (page === 'mybookings') renderMyBookings(main);
  else if (page === 'admin')      renderAdmin(main);
}