document.addEventListener('invalid', function (event) {
    const field = event.target;
    const state = field.validity;
    let message = '';

    if (state.valueMissing) {
        message = 'Please fill out this field.';
    } else if (state.typeMismatch && field.type === 'email') {
        message = 'Please enter a valid email address.';
    } else if (state.badInput || state.typeMismatch) {
        message = 'Please enter a valid value.';
    } else if (state.stepMismatch) {
        message = 'Please enter a valid value.';
    } else if (state.rangeUnderflow) {
        message = 'Value must be greater than or equal to ' + field.min + '.';
    } else if (state.rangeOverflow) {
        message = 'Value must be less than or equal to ' + field.max + '.';
    } else if (state.tooLong) {
        message = 'Please use no more than ' + field.maxLength + ' characters.';
    }

    if (message) {
        field.setCustomValidity(message);
    }
}, true);

document.addEventListener('input', function (event) {
    if (event.target.setCustomValidity) {
        event.target.setCustomValidity('');
    }
}, true);
