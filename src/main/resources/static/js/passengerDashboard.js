document.addEventListener("DOMContentLoaded", async () => {
    const flightsContainer = document.getElementById("flightsContainer");
    const logoutBtn = document.getElementById("logoutBtn");
    const paymentForm = document.getElementById("paymentForm");
    const flightCodeInput = document.getElementById("flightId");
    const seatCountInput = document.getElementById("seatCount");
    const totalPriceElement = document.getElementById("totalPrice");

    let selectedSeats = [];
    let selectedFlight = null;

    logoutBtn.addEventListener("click", () => {
        localStorage.removeItem("jwtToken");
        window.location.href = "index.html";
    });

    async function fetchFlights() {
        try {
            const response = await fetch("http://localhost:8080/flight/all", {
                headers: {"Authorization": `Bearer ${localStorage.getItem("jwtToken")}`}
            });
            if (!response.ok) throw new Error("Failed to fetch flights");

            const flights = await response.json();
            renderFlights(flights);
        } catch (error) {
            console.error("🚨 Error fetching flights:", error);
        }
    }

    function renderFlights(flights) {
        flightsContainer.innerHTML = "";
        const now = new Date();

        flights.forEach(flight => {
            const departureDate = new Date(flight.formattedDepartureTime);
            const isPastFlight = departureDate < now;

            const pastFlightIcon = isPastFlight
                ? '<i class="fas fa-times-circle" style="color:red;"></i>'
                : '<i class="fas fa-check-circle" style="color:green;"></i>';

            const flightItem = document.createElement("li");
            flightItem.classList.add("flight-item");
            flightItem.innerHTML = `
                <div class="flight-info">
                    <strong>${flight.name} (${flight.flightCode})</strong> - ${flight.description}
                    <div>🛫 Departure: ${flight.formattedDepartureTime} ${pastFlightIcon}</div>
                    <div>🛬 Arrival: ${flight.formattedArrivalTime}</div>
                    <button class="btn toggle-seats" data-flight-code="${flight.flightCode}" ${
                isPastFlight ? 'disabled style="background-color: gray; cursor: not-allowed;"' : ""
            }>View Seats</button>
                    <div class="seat-container hidden"></div>
                </div>
            `;

            const seatContainer = flightItem.querySelector(".seat-container");
            const toggleBtn = flightItem.querySelector(".toggle-seats");

            toggleBtn.addEventListener("click", async () => {
                if (isPastFlight) {
                    alert("⛔ This flight has already departed. You cannot book tickets.");
                    return;
                }

                if (selectedFlight !== flight.flightCode) {
                    closeAllSeatSections();
                    resetSeatSelection();
                }

                selectedFlight = flight.flightCode;
                flightCodeInput.value = selectedFlight;

                if (!seatContainer.classList.contains("hidden")) {
                    seatContainer.classList.add("hidden");
                    return;
                }

                seatContainer.innerHTML = "Loading...";
                const seats = await fetchSeats(flight.flightCode);
                seatContainer.innerHTML = "";

                if (seats.length > 0) {
                    const seatGrid = document.createElement("div");
                    seatGrid.classList.add("seat-grid");

                    seats.forEach(seat => {
                        const seatDiv = document.createElement("div");
                        seatDiv.classList.add("seat");

                        if (seat.seatStatus === "Available") {
                            seatDiv.innerHTML = `
                                <label class="seat-label">
                                    <i class="fas fa-chair" style="color:green;"></i>
                                    <input type="checkbox" value="${seat.seatNumber}" data-price="${seat.seatPrice}">
                                    ${seat.seatNumber} - $${seat.seatPrice}
                                </label>
                            `;
                            seatDiv.addEventListener("click", () => {
                                const checkbox = seatDiv.querySelector('input[type="checkbox"]');
                                if (checkbox) {
                                    checkbox.checked = !checkbox.checked;
                                    updateSelectedSeats();
                                }
                            });
                        } else {
                            seatDiv.innerHTML = `
                                <label class="seat-label" style="color:red;">
                                    <i class="fas fa-chair"></i> ${seat.seatNumber} - Sold
                                </label>
                            `;
                        }
                        seatGrid.appendChild(seatDiv);
                    });

                    seatContainer.appendChild(seatGrid);

                    seatContainer.querySelectorAll("input[type='checkbox']").forEach(checkbox => {
                        checkbox.addEventListener("change", updateSelectedSeats);
                    });
                } else {
                    seatContainer.innerHTML = "<p>No available seats</p>";
                }

                seatContainer.classList.toggle("hidden");
            });

            flightsContainer.appendChild(flightItem);
        });
    }

    function closeAllSeatSections() {
        document.querySelectorAll(".seat-container").forEach(seatSection => {
            seatSection.classList.add("hidden");
        });
    }

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

    function updateSelectedSeats() {
        selectedSeats = Array.from(document.querySelectorAll(".seat input:checked"))
            .map(input => input.value);

        seatCountInput.value = selectedSeats.length;
        updateTotalPrice();
    }

    async function updateTotalPrice() {
        if (selectedSeats.length === 0) {
            totalPriceElement.innerText = "$0.00";
            return;
        }


        try {
            const response = await fetch(`http://localhost:8080/payment/calculate/${selectedFlight}`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${localStorage.getItem("jwtToken")}`
                },
                body: JSON.stringify(selectedSeats)
            });

            if (!response.ok) throw new Error("Failed to calculate total price");

            const totalPrice = await response.json();
            totalPriceElement.innerText = `$${totalPrice.toFixed(2)}`;
        } catch (error) {
            console.error("🚨 Error calculating total price:", error);
        }
    }

    function resetSeatSelection() {
        document.querySelectorAll(".seat input").forEach(input => input.checked = false);
        selectedSeats = [];
        seatCountInput.value = 0;
        totalPriceElement.innerText = "$0.00";
    }


    paymentForm.addEventListener("submit", (event) => {
        event.preventDefault();
        if (selectedSeats.length === 0) {
            alert("Please select at least one seat.");
            return;
        }
        paymentForm.style.display = "none";
        paymentDetails.style.display = "block";
    });

    confirmPaymentForm.addEventListener("submit", async (event) => {
        event.preventDefault();

        const cardHolderName = document.getElementById("cardHolderName").value;
        const cardNumber = document.getElementById("cardNumber").value;
        const cardExpireMonth = document.getElementById("cardExpireMonth").value;
        const cardExpireYear = document.getElementById("cardExpireYear").value;
        const cardCvc = document.getElementById("cardCvc").value;

        try {
            const response = await fetch(`http://localhost:8080/payment/${selectedFlight}/`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${localStorage.getItem("jwtToken")}`
                },
                body: JSON.stringify({
                    seatNumbers: selectedSeats,
                    cardHolderName,
                    cardNumber,
                    cardExpireMonth,
                    cardExpireYear,
                    cardCvc
                })
            });

            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.message || "Payment failed.");
            }

            alert("✅ Payment successful!");
            window.location.reload();
        } catch (error) {
            console.error("🚨 Error processing payment:", error);
            alert("❌ Payment failed! Please check your details and try again.");
        }
    });
    fetchFlights();
});