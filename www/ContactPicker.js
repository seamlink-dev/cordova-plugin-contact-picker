var exec = require("cordova/exec");
var PLUGIN_NAME = "ContactPicker";

module.exports = {
    requestContact: function(settings) {
        return new Promise(function(resolve, reject) {
            exec(resolve, reject, PLUGIN_NAME, "requestContact", [settings || {}]);
        });
    }
};
