const params = new URLSearchParams(window.location.search);

if (params.get("payflow_action") === "register") {
  const registrationLink = document.querySelector("#kc-registration a");
  registrationLink?.click();
}
