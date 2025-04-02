document.addEventListener("DOMContentLoaded", () => {
  const amounts = document.querySelectorAll(".amount");
  if (amounts.length > 0) {
    amounts.forEach((amount) => {
      let value = parseFloat(amount.textContent);

      if (value % 1 === 0) {
        amount.textContent = value.toLocaleString("fr-FR", {
          style: "currency",
          currency: "EUR",
          minimumFractionDigits: 0,
        });
      } else {
        amount.textContent = value.toLocaleString("fr-FR", {
          style: "currency",
          currency: "EUR",
          minimumFractionDigits: 2,
        });
      }
    });
  }

  const amountInput = document.getElementById("transfer-amount");
  if (amountInput) {
    amountInput.addEventListener("input", (event) => {
      let value = event.target.value;
      const regex = /^\d*(,\d{0,2})?$/;
      if (!regex.test(value)) value = value.slice(0, -1);
      event.target.value = value;
    });
  }
});
