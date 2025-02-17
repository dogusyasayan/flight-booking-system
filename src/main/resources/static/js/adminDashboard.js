document.addEventListener("DOMContentLoaded", async () => {
    const flightsContainer = document.getElementById("flightsContainer");
    const logoutBtn = document.getElementById("logoutBtn");
    const formTabs = document.querySelectorAll(".form-tab");
    const formContents = document.querySelectorAll(".form-content");
    const createFlightForm = document.getElementById("createFlightForm");
    const updateFlightForm = document.getElementById("updateFlightForm");
    const deleteFlightForm = document.getElementById("deleteFlightForm");
    const addSeatsForm = document.getElementById("addSeatsForm");

    // Logout
    logoutBtn.addEventListener("click", () => {
        localStorage.removeItem("jwtToken");
        window.location.href = "index.html";
    });

    // Tab Navigation
    formTabs.forEach(tab => {
        tab.addEventListener("click", () => {
            const targetForm = document.getElementById(tab.dataset.form);
            formTabs.forEach(t => t.classList.remove("active"));
            formContents.forEach(c => c.classList.remove("active"));
            tab.classList.add("active");
            targetForm.classList.add("active");
        });
    });

    // Fetch Flights
    async function fetchFlights() {
        try {
            const response = await fetch("http://localhost:8080/flight/all", {
                headers: {"Authorization": `Bearer ${localStorage.getItem("jwtToken")}`}
            });
            if (!response.ok) throw new Error(`Failed to fetch flights: ${response.status}`);

            const flights = await response.json();
            renderFlights(flights);
        } catch (error) {
            console.error("🚨 Error fetching flights:", error);
        }
    }


    // Render Flights and Attach Click Event
    function renderFlights(flights) {
        flightsContainer.innerHTML = flights.length ? "" : "<p>No flights available.</p>";

        flights.forEach(flight => {
            const [datePart, timePart] = flight.formattedDepartureTime.split(" ");
            const [day, month, year] = datePart.split(".").map(num => parseInt(num, 10));
            const [hour, minute] = timePart.split(":").map(num => parseInt(num, 10));

            const departureDate = new Date(year, month - 1, day, hour, minute); // JS'te aylar 0 tabanlıdır
            const currentDate = new Date(); // Şu anki tarih

            const pastFlightIcon = departureDate < currentDate
                ? '<i class="fas fa-times-circle" style="color:red;"></i>'  // Geçmiş uçuşlar için kırmızı
                : '<i class="fas fa-check-circle" style="color:green;"></i>'; // Gelecek uçuşlar için yeşil

            const flightItem = document.createElement("li");
            flightItem.classList.add("flight-item");
            flightItem.innerHTML = `
            <div class="flight-info">
                <strong>${flight.name} (${flight.flightCode})</strong> - ${flight.description}
                <div>🛫 ${flight.formattedDepartureTime} ${pastFlightIcon}</div>
                <div>🛬 ${flight.formattedArrivalTime}</div>
                <button class="btn toggle-seats" data-flight-code="${flight.flightCode}" style="margin-top: 10px;">View Seats</button>
                <div class="seat-container hidden"></div>
            </div>
        `;

            flightItem.dataset.flightCode = flight.flightCode;
            flightItem.dataset.flightName = flight.name;
            flightItem.dataset.description = flight.description;
            flightItem.dataset.departureTime = flight.formattedDepartureTime;
            flightItem.dataset.arrivalTime = flight.formattedArrivalTime;

            flightItem.addEventListener("click", () => populateForms(flightItem));

            const toggleSeatsBtn = flightItem.querySelector(".toggle-seats");
            const seatContainer = flightItem.querySelector(".seat-container");

            toggleSeatsBtn.addEventListener("click", async () => {
                if (!seatContainer.classList.contains("hidden")) {
                    seatContainer.classList.add("hidden");
                    seatContainer.innerHTML = "";
                    return;
                }

                seatContainer.innerHTML = "<p style='margin-top: 10px;'>Loading seats...</p>";
                seatContainer.classList.remove("hidden");

                const seats = await fetchSeats(flight.flightCode);
                if (seats.length === 0) {
                    seatContainer.innerHTML = "<p>No seats available.</p>";
                    return;
                }

                seatContainer.innerHTML = seats.map(seat => `
                <div class="seat" style="color: ${seat.seatStatus === "Available" ? "green" : "red"}; font-weight: bold; margin-top: 5px;">
                    <i class="fas fa-chair"></i> ${seat.seatNumber} - $${seat.seatPrice}
                </div>
            `).join("");
            });

            flightsContainer.appendChild(flightItem);
        });
    }

    function populateForms(flightItem) {
        document.getElementById("updateFlightCode").value = flightItem.dataset.flightCode;
        document.getElementById("updateFlightName").value = flightItem.dataset.flightName;
        document.getElementById("updateFlightDescription").value = flightItem.dataset.description;
        document.getElementById("updateDepartureTime").value = formatDate(flightItem.dataset.departureTime);
        document.getElementById("updateArrivalTime").value = formatDate(flightItem.dataset.arrivalTime);

        document.getElementById("deleteFlightCode").value = flightItem.dataset.flightCode;
        document.getElementById("seatFlightCode").value = flightItem.dataset.flightCode;
    }

    // **Create Flight**
    document.getElementById("createFlightForm").addEventListener("submit", async (e) => {
        e.preventDefault();

        const flightData = {
            name: document.getElementById("flightName").value.trim(),
            description: document.getElementById("flightDescription").value.trim(),
            departureTime: formatDate(document.getElementById("departureTime").value),
            arrivalTime: formatDate(document.getElementById("arrivalTime").value)
        };

        if (!flightData.name || !flightData.departureTime || !flightData.arrivalTime) {
            alert("🚨 Flight Name, Departure Time ve Arrival Time zorunludur!");
            return;
        }

        try {
            const response = await fetch("http://localhost:8080/flight", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${localStorage.getItem("jwtToken")}`
                },
                body: JSON.stringify(flightData)
            });

            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.message || `Flight creation failed: ${response.status}`);
            }

            alert("✅ Flight created successfully!");
            fetchFlights(); // Listeyi güncelle
        } catch (error) {
            console.error("🚨 Error creating flight:", error);
            alert(error.message);
        }
    });

    // Flight Update Submission
    updateFlightForm.addEventListener("submit", async (e) => {
        e.preventDefault();

        const flightCode = document.getElementById("updateFlightCode").value.trim();
        const updateData = {
            name: document.getElementById("updateFlightName").value.trim() || null,
            description: document.getElementById("updateFlightDescription").value.trim() || null,
            departureTime: document.getElementById("updateDepartureTime").value ? formatDate(document.getElementById("updateDepartureTime").value) : null,
            arrivalTime: document.getElementById("updateArrivalTime").value ? formatDate(document.getElementById("updateArrivalTime").value) : null
        };

        await sendRequest("PUT", `flight/${flightCode}`, updateData, "Flight updated successfully!");
    });

    // Format Date
    function formatDate(dateString) {
        return dateString.replace("T", " ");
    }

    // Send HTTP Request
    async function sendRequest(method, endpoint, body, successMessage) {
        try {
            const response = await fetch(`http://localhost:8080/${endpoint}`, {
                method,
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${localStorage.getItem("jwtToken")}`
                },
                body: body ? JSON.stringify(body) : null
            });
            if (!response.ok) throw new Error(`${method} failed: ${response.status}`);
            alert(`✅ ${successMessage}`);
            fetchFlights();
        } catch (error) {
            console.error(`🚨 Error in ${method} request:`, error);
            alert(error.message);
        }
    }

    // **Delete Flight**
    deleteFlightForm.addEventListener("submit", async (e) => {
        e.preventDefault();
        const flightCode = document.getElementById("deleteFlightCode").value.trim();
        await sendRequest("DELETE", `flight/${flightCode}`, null, "Flight deleted successfully!");
    });

    // **Add Seats**
    addSeatsForm.addEventListener("submit", async (e) => {
        e.preventDefault();
        const flightCode = document.getElementById("seatFlightCode").value.trim();
        const seatData = {
            numberOfSeats: parseInt(document.getElementById("numberOfSeats").value),
            seatPrice: parseFloat(document.getElementById("seatPrice").value)
        };
        await sendRequest("POST", `seat/${flightCode}`, seatData, "Seats added successfully!");
    });

    // **View Seats**
    async function fetchSeats(flightCode) {
        try {
            const response = await fetch(`http://localhost:8080/seat/${flightCode}`, {
                headers: {"Authorization": `Bearer ${localStorage.getItem("jwtToken")}`}
            });
            return response.ok ? await response.json() : [];
        } catch (error) {
            console.error("🚨 Error fetching seats:", error);
            return [];
        }
    }

    fetchFlights();
});
