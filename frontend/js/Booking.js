function openPaymentFromBtn(btn) {
    openPayment(JSON.parse(btn.getAttribute('data-booking')));
  }
  
  function openPayment(booking) {
    booking.userId = currentUser.id;
    pendingBooking = booking;
  
    const nights = Math.round((new Date(booking.checkOut) - new Date(booking.checkIn)) / 86400000);
    const total  = (nights * booking.pricePerNight).toFixed(2);
  
    document.getElementById('pay-summary').innerHTML = `
      <div class="row"><span>Hotel</span><span>${booking.hotelName}</span></div>
      <div class="row"><span>Room</span><span>${booking.roomNumber} (${booking.roomType})</span></div>
      <div class="row"><span>Check-in</span><span>${booking.checkIn}</span></div>
      <div class="row"><span>Check-out</span><span>${booking.checkOut}</span></div>
      <div class="row"><span>Duration</span><span>${nights} night${nights > 1 ? 's' : ''}</span></div>
      <div class="row total"><span>Total</span><span>$${total}</span></div>`;
  
    document.getElementById('payment-form-view').style.display = 'block';
    document.getElementById('payment-success-view').classList.remove('show');
    ['pay-name', 'pay-card', 'pay-expiry', 'pay-cvv'].forEach(id => document.getElementById(id).value = '');
    document.getElementById('pay-msg').textContent = '';
    document.getElementById('pay-btn').textContent = 'Pay Now';
    document.getElementById('pay-btn').disabled    = false;
    document.getElementById('payment-overlay').classList.add('open');
  }
  
  function closePayment() {
    document.getElementById('payment-overlay').classList.remove('open');
  }
  
  async function processPayment() {
    const btn  = document.getElementById('pay-btn');
    const msg  = document.getElementById('pay-msg');
    const name = document.getElementById('pay-name').value.trim();
    const card = document.getElementById('pay-card').value.trim();
    const exp  = document.getElementById('pay-expiry').value.trim();
    const cvv  = document.getElementById('pay-cvv').value.trim();
  
    if (!name || !card || !exp || !cvv) { msg.textContent = 'Please fill in all payment details'; return; }
  
    btn.disabled = true;
    btn.innerHTML = '<span class="spinner"></span> Processing…';
    await new Promise(r => setTimeout(r, 1500));
  
    try {
      const res  = await fetch(`${API}/bookings`, {
        method: 'POST', headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          userId:   String(pendingBooking.userId),
          roomId:   String(pendingBooking.roomId),
          checkIn:  pendingBooking.checkIn,
          checkOut: pendingBooking.checkOut
        })
      });
      const data = await res.json();
  
      if (!res.ok) {
        msg.textContent = data.error || 'Booking failed';
        btn.disabled = false; btn.textContent = 'Pay Now';
        return;
      }
  
      const nights = Math.round((new Date(pendingBooking.checkOut) - new Date(pendingBooking.checkIn)) / 86400000);
      const total  = (nights * pendingBooking.pricePerNight).toFixed(2);
      document.getElementById('success-detail').textContent =
        `Booking #${data.bookingID} — ${pendingBooking.hotelName}, Room ${pendingBooking.roomNumber} · $${total}`;
  
      document.getElementById('payment-form-view').style.display = 'none';
      document.getElementById('payment-success-view').classList.add('show');
      setStatus(`Booking #${data.bookingID} confirmed!`, true);
    } catch {
      msg.textContent = 'Server error. Please try again.';
      btn.disabled = false; btn.textContent = 'Pay Now';
    }
  }
  
  async function renderMyBookings(el) {
    el.innerHTML = `
      <div class="page-header">
        <h2>My Bookings</h2>
        <p>Your upcoming and past reservations</p>
      </div>
      <div class="bookings-list"><div class="empty-state"><div class="spinner"></div></div></div>`;
  
    try {
      const bookings = await fetch(`${API}/bookings/user/${currentUser.id}`).then(r => r.json());
      const list = el.querySelector('.bookings-list');
  
      if (bookings.length === 0) {
        list.innerHTML = `<div class="empty-state"><p>No bookings yet. Browse hotels to make your first reservation!</p></div>`;
        setStatus('No bookings yet', false);
        return;
      }
  
      list.innerHTML = bookings.map(b => `
        <div class="booking-card">
          <div class="booking-id">
            <div class="num">#${b.bookingId}</div>
            <div class="lbl">Booking</div>
          </div>
          <div class="booking-info">
            <h4>${b.hotelName}</h4>
            <p>Room ${b.roomNumber} · <span class="badge badge-${b.roomType.toLowerCase()}">${b.roomType}</span></p>
          </div>
          <div class="booking-dates">
            <div class="dates">${b.checkIn} → ${b.checkOut}</div>
            <div class="nights">${b.nights} night${b.nights > 1 ? 's' : ''}</div>
            <div class="total">$${Number(b.totalPrice).toFixed(2)}</div>
          </div>
        </div>`).join('');
  
      setStatus(`${bookings.length} booking(s)`, true);
    } catch {
      setStatus('Failed to load bookings', false);
    }
  }
  
  function fmtCard(input) {
    let v = input.value.replace(/\D/g, '').substring(0, 16);
    input.value = v.replace(/(.{4})/g, '$1 ').trim();
  }
  
  function fmtExpiry(input) {
    let v = input.value.replace(/\D/g, '').substring(0, 4);
    if (v.length >= 2) v = v.substring(0, 2) + ' / ' + v.substring(2);
    input.value = v;
  }
  
  document.getElementById('payment-overlay').addEventListener('click', e => {
    if (e.target === document.getElementById('payment-overlay')) closePayment();
  });