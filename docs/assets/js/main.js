
$("#menu-toggle").click(function(e) {
    e.preventDefault();
    $("#sidebar-wrapper").toggleClass("active");
});

$("#quickstart-guides").click(function(e) {
    e.preventDefault();
    $("#quickstart-guides-items").toggleClass("active");
    $("#quickstart-guides-chevron").toggleClass("chevron-active");
});
