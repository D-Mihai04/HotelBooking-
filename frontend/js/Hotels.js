async function renderHotels(el) {
    el.innerHTML = `
      <div class="page-header">
        <h2>Browse Hotels</h2>
        <p>Click on any hotel to view available rooms and make a booking</p>
      </div>
      <div style="padding:1rem 2rem 0">
        <div style="display:flex;gap:.75rem;max-width:500px">
          <input id="hotel-search" type="text" placeholder="Search by name or city…" oninput="filterHotels()" />
        </div>
      </div>
      <div class="hotels-grid" id="hotels-grid"><div class="empty-state"><div class="spinner"></div></div></div>`;
  
    try {
      const hotels = await fetch(`${API}/hotels`).then(r => r.json());
      window._allHotels = hotels;
      document.getElementById('hotels-grid').innerHTML = hotelsHtml(hotels);
      setStatus(`${hotels.length} hotels available`, true);
    } catch {
      document.getElementById('hotels-grid').innerHTML = `<p class="err-msg" style="padding:1rem">Could not load hotels.</p>`;
    }
  }
  
  function hotelsHtml(hotels) {
    if (hotels.length === 0) return `<div class="empty-state"><p>No hotels found.</p></div>`;
    return hotels.map(h => `
      <div class="hotel-card" onclick="renderHotelDetail(${h.id})">
        <div class="hotel-card-img" style="background-image:url('images/hotel${h.id}.jpg'); background-size:cover; background-position:center;"></div>
        <div class="hotel-card-body">
          <h3>${h.name}</h3>
          <div class="hotel-card-meta">
            <span>📍 ${h.address}</span>
            <span>📞 ${h.phone}</span>
          </div>
        </div>
        <div class="hotel-card-footer">
          <span>${h.email}</span>
        </div>
      </div>`).join('');
  }
  
  async function filterHotels() {
    const query = document.getElementById('hotel-search')?.value.trim();
    const grid  = document.getElementById('hotels-grid');
    if (!grid) return;
  
    if (!query) {
      grid.innerHTML = hotelsHtml(window._allHotels || []);
      setStatus(`${(window._allHotels || []).length} hotels available`, true);
      return;
    }
  
    const hotels = await fetch(`${API}/hotels/search?query=${encodeURIComponent(query)}`).then(r => r.json());
    grid.innerHTML = hotelsHtml(hotels);
    setStatus(`${hotels.length} result(s) for "${query}"`, hotels.length > 0);
  }
  async function renderHotelDetail(hotelId) {
    const main = document.getElementById('main-content');
    main.innerHTML = `<div class="empty-state"><div class="spinner"></div></div>`;
  
    const hotel     = await fetch(`${API}/hotels/${hotelId}`).then(r => r.json());
    const tomorrow  = dateStr(1);
    const threeDays = dateStr(3);
  
    main.innerHTML = `
      <div class="detail-header">
        <button class="back-btn" onclick="showPage('hotels')">← Back to Hotels</button>
        <h2>${hotel.name}</h2>
        <div class="hotel-meta-row">
          <span>📍 ${hotel.address}</span>
          <span>📞 ${hotel.phone}</span>
          <span>${hotel.email}</span>
        </div>
      </div>
      <div class="date-bar">
        <div>
          <label>Check-in</label>
          <input type="date" id="checkin-input" value="${tomorrow}" min="${dateStr(0)}" />
        </div>
        <div>
          <label>Check-out</label>
          <input type="date" id="checkout-input" value="${threeDays}" />
        </div>
        <button class="btn btn-accent" onclick="searchRooms(${hotelId})">Search Availability</button>
      </div>
      <div class="rooms-list" id="rooms-list">
        <div class="empty-state">
          <p>Select your dates above and click Search to see available rooms</p>
        </div>
      </div>`;
  }
  
  async function searchRooms(hotelId) {
    const checkIn  = document.getElementById('checkin-input').value;
    const checkOut = document.getElementById('checkout-input').value;
    const list     = document.getElementById('rooms-list');
  
    if (!checkIn || !checkOut) { setStatus('Please select both dates', false); return; }
    if (checkIn >= checkOut)   { setStatus('Check-out must be after check-in', false); return; }
  
    list.innerHTML = `<div class="empty-state"><div class="spinner"></div></div>`;
  
    const [rooms, hotel] = await Promise.all([
      fetch(`${API}/hotels/${hotelId}/rooms/available?checkIn=${checkIn}&checkOut=${checkOut}`).then(r => r.json()),
      fetch(`${API}/hotels/${hotelId}`).then(r => r.json())
    ]);
  
    if (rooms.length === 0) {
      list.innerHTML = `<div class="empty-state"><p>No rooms available for those dates. Try different dates.</p></div>`;
      setStatus('No rooms available for those dates', false);
      return;
    }
  
    list.innerHTML = rooms.map(r => {
      const bookingData = JSON.stringify({
        roomId: r.id, checkIn, checkOut,
        hotelName: hotel.name, roomNumber: r.roomNumber,
        roomType: r.type, pricePerNight: r.pricePerNight
      });
      return `
        <div class="room-row">
          <div class="room-icon">${r.type[0]}</div>
          <div class="room-info">
            <h4>Room ${r.roomNumber}</h4>
            <p>Capacity: ${r.capacity} guest${r.capacity > 1 ? 's' : ''}</p>
            <div class="room-badges">
              <span class="badge badge-${r.type.toLowerCase()}">${r.type}</span>
              <span class="badge badge-cap">${r.capacity} guests</span>
            </div>
          </div>
          <div class="room-price">
            <div class="price">$${r.pricePerNight.toFixed(2)}</div>
            <div class="per">per night</div>
          </div>
          <button class="book-room-btn" data-booking='${bookingData}' onclick="openPaymentFromBtn(this)">Book →</button>
        </div>`;
    }).join('');
  
    setStatus(`${rooms.length} room(s) available at ${hotel.name}`, true);
  }