var exec = require("cordova/exec");
var PLUGIN_NAME = "ContactPicker";

module.exports = {
    open: function(settings) {
        return new Promise(function(resolve, reject) {
            exec(resolve, reject, PLUGIN_NAME, "open", [settings]);
        });
    }
};
