const openModalBtn = document.getElementById('openModal');
const closeModalBtn = document.getElementById('closeModal');
const modalOverlay = document.getElementById('overlay');

openModalBtn.addEventListener("click", () => {
    modalOverlay.style.display = "flex";
    modalOverlay.style.zIndex = "5";
})

closeModalBtn.addEventListener("click", () => {
    modalOverlay.style.display = "none";
    modalOverlay.style.zIndex = "-1";
})