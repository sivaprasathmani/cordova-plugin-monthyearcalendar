var exec = require('cordova/exec');

exports.showCalendar = function (arg0, success, error) {
    exec(success, error, 'MonthYearCalendar', 'showCalendar', [arg0]);
};
