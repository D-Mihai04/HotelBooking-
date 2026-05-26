async function renderAdmin(el) {
    el.innerHTML = `
      <div class="page-header">
        <h2>Admin Panel</h2>
        <p>Manage rooms and bookings for your hotel</p>
      </div>
      <div class="admin-section">
        <div>
          <div class="admin-section-title">
            <span>Rooms</span>
            <button class="btn btn-accent" style="font-size:.85rem;padding:.5rem 1rem" onclick="toggleAddRoomForm()">+ Add Room</button>
          </div>
          <div class="add-room-form" id="add-room-form">
            <h4>New Room</h4>
            <div class="form-row">
              <div class="field-group">
                <label>Room Number</label>
                <input id="nr-number" type="text" placeholder="101" />
              </div>
              <div class="field-group">
                <label>Type</label>
                <select id="nr-type">
                  <option value="SINGLE">SINGLE</option>
                  <option value="DOUBLE">DOUBLE</option>
                  <option value="SUITE">SUITE</option>
                </select>
              </div>
              <div class="field-group">
                <label>Capacity</label>
                <input id="nr-capacity" type="number" placeholder="2" min="1" />
              </div>
              <div class="field-group">
                <label>Price / Night ($)</label>
                <input id="nr-price" type="number" placeholder="99.99" step="0.01" />
              </div>
            </div>
            <div class="form-actions">
              <button class="btn btn-accent" style="font-size:.88rem;padding:.6rem 1.2rem" onclick="submitAddRoom()">Save Room</button>
              <button class="btn btn-ghost"  style="font-size:.88rem;padding:.6rem 1.2rem" onclick="toggleAddRoomForm()">Cancel</button>
            </div>
            <div class="err-msg" id="add-room-msg" style="margin-top:.5rem"></div>
          </div>
          <div id="admin-rooms-list"><div class="empty-state"><div class="spinner"></div></div></div>
        </div>
  
        <div>
          <div class="admin-section-title">All Bookings</div>
          <div id="admin-bookings-list"><div class="empty-state"><div class="spinner"></div></div></div>
        </div>
      </div>`;
  
    loadAdminRooms();
    loadAdminBookings();
  }
  
  async function loadAdminRooms() {
    const el = document.getElementById('admin-rooms-list');
    if (!el) return;
  
    const rooms = await fetch(`${API}/admin/hotels/${currentUser.hotelId}/rooms?adminId=${currentUser.id}`).then(r => r.json());
  
    if (!rooms.length) {
      el.innerHTML = `<div class="empty-state"><p>No rooms yet.</p></div>`;
      return;
    }
  
    el.innerHTML = `
      <div class="tbl-wrap">
        <table>
          <thead>
            <tr><th>Room</th><th>Type</th><th>Capacity</th><th>Price/Night</th><th>Status</th><th>Actions</th></tr>
          </thead>
          <tbody>${rooms.map(r => roomRowHtml(r)).join('')}</tbody>
        </table>
      </div>`;
  }
  
  function roomRowHtml(r) {
    return `
      <tr id="room-row-${r.id}">
        <td><strong>${r.roomNumber}</strong></td>
        <td><span class="badge badge-${r.type.toLowerCase()}">${r.type}</span></td>
        <td>${r.capacity}</td>
        <td>$${r.pricePerNight.toFixed(2)}</td>
        <td>${r.available
          ? '<span class="badge badge-single">Available</span>'
          : '<span class="badge badge-unavail">Unavailable</span>'}</td>
        <td style="display:flex;gap:.4rem;flex-wrap:wrap">
          <button class="action-btn action-btn-edit" onclick='showEditRoom(${JSON.stringify(r)})'>Edit</button>
          <button class="action-btn action-btn-toggle ${r.available ? 'disable' : 'enable'}"
                  onclick="toggleAvailability(${r.id}, ${r.available})">
            ${r.available ? 'Disable' : 'Enable'}
          </button>
          <button class="action-btn action-btn-delete" onclick="deleteRoom(${r.id})">Delete</button>
        </td>
      </tr>`;
  }
  
  async function loadAdminBookings() {
    const el = document.getElementById('admin-bookings-list');
    if (!el) return;
  
    const bookings = await fetch(`${API}/admin/hotels/${currentUser.hotelId}/bookings?adminId=${currentUser.id}`).then(r => r.json());
  
    if (!bookings.length) {
      el.innerHTML = `<div class="empty-state"><p>No bookings for this hotel yet.</p></div>`;
      return;
    }
  
    el.innerHTML = `
      <div class="tbl-wrap">
        <table>
          <thead>
            <tr><th>#</th><th>Room</th><th>Customer ID</th><th>Check-in</th><th>Check-out</th><th>Nights</th><th></th></tr>
          </thead>
          <tbody>
            ${bookings.map(b => `
              <tr id="booking-row-${b.bookingId}">
                <td>#${b.bookingId}</td>
                <td>${b.roomNumber} <span class="badge badge-${b.roomType.toLowerCase()}">${b.roomType}</span></td>
                <td>${b.customerId}</td>
                <td>${b.checkIn}</td>
                <td>${b.checkOut}</td>
                <td>${b.nights}</td>
                <td><button class="action-btn action-btn-cancel" onclick="cancelBooking(${b.bookingId})">Cancel</button></td>
              </tr>`).join('')}
          </tbody>
        </table>
      </div>`;
  }
  
  function toggleAddRoomForm() {
    const f = document.getElementById('add-room-form');
    f.style.display = f.style.display === 'block' ? 'none' : 'block';
  }
  
  async function submitAddRoom() {
    const number   = document.getElementById('nr-number').value.trim();
    const type     = document.getElementById('nr-type').value;
    const capacity = document.getElementById('nr-capacity').value;
    const price    = document.getElementById('nr-price').value;
    const msg      = document.getElementById('add-room-msg');
  
    if (!number || !capacity || !price) { msg.textContent = 'Please fill all fields.'; return; }
  
    const res = await fetch(`${API}/admin/hotels/${currentUser.hotelId}/rooms?adminId=${currentUser.id}`, {
      method: 'POST', headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ roomNumber: number, type, capacity, pricePerNight: price })
    });
  
    if (res.ok) {
      document.getElementById('add-room-form').style.display = 'none';
      loadAdminRooms();
      setStatus('Room added successfully', true);
    } else {
      msg.textContent = 'Failed to add room.';
    }
  }
  
  function showEditRoom(room) {
    const row = document.getElementById('room-row-' + room.id);
    row.innerHTML = `
      <td colspan="6">
        <div class="inline-edit">
          <div class="field-group">
            <label>Room #</label>
            <input id="er-number-${room.id}" type="text" value="${room.roomNumber}" />
          </div>
          <div class="field-group">
            <label>Type</label>
            <select id="er-type-${room.id}">
              <option ${room.type === 'SINGLE' ? 'selected' : ''}>SINGLE</option>
              <option ${room.type === 'DOUBLE' ? 'selected' : ''}>DOUBLE</option>
              <option ${room.type === 'SUITE'  ? 'selected' : ''}>SUITE</option>
            </select>
          </div>
          <div class="field-group">
            <label>Capacity</label>
            <input id="er-capacity-${room.id}" type="number" value="${room.capacity}" />
          </div>
          <div class="field-group">
            <label>Price/Night</label>
            <input id="er-price-${room.id}" type="number" value="${room.pricePerNight}" step="0.01" />
          </div>
          <div style="display:flex;gap:.4rem;align-self:flex-end">
            <button class="action-btn action-btn-edit" onclick="submitEditRoom(${room.id}, ${room.available})">Save</button>
            <button class="action-btn" onclick="loadAdminRooms()">Cancel</button>
          </div>
        </div>
      </td>`;
  }
  
  async function submitEditRoom(roomId, available) {
    const body = {
      roomNumber:    document.getElementById(`er-number-${roomId}`).value.trim(),
      type:          document.getElementById(`er-type-${roomId}`).value,
      capacity:      document.getElementById(`er-capacity-${roomId}`).value,
      pricePerNight: document.getElementById(`er-price-${roomId}`).value,
      available:     String(available)
    };
    const res = await fetch(`${API}/admin/hotels/${currentUser.hotelId}/rooms/${roomId}?adminId=${currentUser.id}`, {
      method: 'PUT', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(body)
    });
    if (res.ok) { loadAdminRooms(); setStatus('Room updated', true); }
    else setStatus('Failed to update room', false);
  }
  
  async function toggleAvailability(roomId, currentlyAvailable) {
    const rooms = await fetch(`${API}/hotels/${currentUser.hotelId}/rooms`).then(r => r.json());
    const room  = rooms.find(r => r.id === roomId);
    if (!room) return;
    const body = {
      roomNumber:    room.roomNumber,
      type:          room.type,
      capacity:      String(room.capacity),
      pricePerNight: String(room.pricePerNight),
      available:     String(!currentlyAvailable)
    };
    const res = await fetch(`${API}/admin/hotels/${currentUser.hotelId}/rooms/${roomId}?adminId=${currentUser.id}`, {
      method: 'PUT', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(body)
    });
    if (res.ok) { loadAdminRooms(); setStatus(`Room ${currentlyAvailable ? 'disabled' : 'enabled'}`, true); }
    else setStatus('Failed to update availability', false);
  }
  
  async function deleteRoom(roomId) {
    if (!confirm('Delete this room? This cannot be undone.')) return;
    const res = await fetch(`${API}/admin/hotels/${currentUser.hotelId}/rooms/${roomId}?adminId=${currentUser.id}`, {
      method: 'DELETE'
    });
    if (res.ok) { loadAdminRooms(); setStatus('Room deleted', true); }
    else setStatus('Cannot delete — room may have active bookings', false);
  }
  
  async function cancelBooking(bookingId) {
    if (!confirm('Cancel this booking?')) return;
    const res = await fetch(`${API}/admin/bookings/${bookingId}?adminId=${currentUser.id}&hotelId=${currentUser.hotelId}`, {
      method: 'DELETE'
    });
    if (res.ok) { loadAdminBookings(); setStatus('Booking cancelled', true); }
    else setStatus('Failed to cancel booking', false);
  }