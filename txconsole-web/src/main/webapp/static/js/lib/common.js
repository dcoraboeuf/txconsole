define(function () {

    var logging = false;

    String.prototype.format = function () {
        var args = arguments;
        return this.replace(/\{\{|\}\}|\{(\d+)\}/g, function (m, n) {
            if (m == "{{") {
                return "{";
            }
            if (m == "}}") {
                return "}";
            }
            return args[n];
        });
    };

    String.prototype.html = function () {
        return $('<i></i>').text(this).html();
    };

    String.prototype.htmlWithLines = function () {
        var text = this.html();
        return text.replace(/\n/g, '<br/>');
    };

    String.prototype.loc = function (args) {
        var code = this;
        var text = l[code];
        if (text != null) {
            return text.format(args);
        } else {
            return "##" + code + "##";
        }
    };

    String.prototype.toCamelCase = function () {
        return this.replace(
            /-([a-z])/g,
            function (m, w) {
                return w.toUpperCase();
            });
    };

    String.prototype.goto = function () {
        var base = document.getElementsByTagName('base');
        var url = this;
        if (base && base[0] && base[0].href) {
            if (base[0].href.substr(base[0].href.length - 1) == '/' && url.charAt(0) == '/') {
                url = url.substr(1);
            }
            url = base[0].href + url;
        }
        location.href = url;
    }

    function log(context) {
        return function (message, args) {
            if (logging && console) {
                if (args) {
                    console.log('[{1}] {0}'.format(message, context), args);
                } else {
                    console.log('[{1}] {0}'.format(message, context));
                }
            }
        }
    }

    function confirmAndCall(text, callback) {
        $('<div>{0}</div>'.format(text)).dialog({
            title: 'general.confirm.title'.loc(),
            dialogClass: 'confirm-dialog',
            modal: true,
            buttons: {
                Ok: function () {
                    $(this).dialog("close");
                    callback();
                },
                Cancel: function () {
                    $(this).dialog("close");
                }
            }
        });
    }

    function showError(text) {
        $('<div>{0}</div>'.format(text.htmlWithLines())).dialog({
            title: 'client.error.title'.loc(),
            modal: true,
            buttons: {
                Ok: function () {
                    $(this).dialog("close");
                }
            }
        });
    }

    // source: http://www.w3schools.com/js/js_cookies.asp
    function getCookie(c_name) {
        var i, x, y, ARRcookies = document.cookie.split(";");
        for (i = 0; i < ARRcookies.length; i++) {
            x = ARRcookies[i].substr(0, ARRcookies[i].indexOf("="));
            y = ARRcookies[i].substr(ARRcookies[i].indexOf("=") + 1);
            x = x.replace(/^\s+|\s+$/g, "");
            if (x == c_name) {
                return unescape(y);
            }
        }
    }

    function tooltips() {
        $('.tooltip-source').tooltip({
            placement: 'bottom'
        });
    }

    function values(base) {
        var data = {};
        $(base).find('input,textarea,select').each(function (index, field) {
            if (field.getAttribute('readonly') != 'readonly' && field.getAttribute('disabled') != 'disabled') {
                var name = field.getAttribute('name');
                var value = field.value;
                data[name] = value;
            }
        });
        return data;
    }

    function deparam(params, coerce) {
        var obj = {},
            coerce_types = { 'true': !0, 'false': !1, 'null': null };

        // Iterate over all name=value pairs.
        $.each(params.replace(/\+/g, ' ').split('&'), function (j, v) {
            var param = v.split('='),
                key = decodeURIComponent(param[0]),
                val,
                cur = obj,
                i = 0,

            // If key is more complex than 'foo', like 'a[]' or 'a[b][c]', split it
            // into its component parts.
                keys = key.split(']['),
                keys_last = keys.length - 1;

            // If the first keys part contains [ and the last ends with ], then []
            // are correctly balanced.
            if (/\[/.test(keys[0]) && /\]$/.test(keys[ keys_last ])) {
                // Remove the trailing ] from the last keys part.
                keys[ keys_last ] = keys[ keys_last ].replace(/\]$/, '');

                // Split first keys part into two parts on the [ and add them back onto
                // the beginning of the keys array.
                keys = keys.shift().split('[').concat(keys);

                keys_last = keys.length - 1;
            } else {
                // Basic 'foo' style key.
                keys_last = 0;
            }

            // Are we dealing with a name=value pair, or just a name?
            if (param.length === 2) {
                val = decodeURIComponent(param[1]);

                // Coerce values.
                if (coerce) {
                    val = val && !isNaN(val) ? +val              // number
                        : val === 'undefined' ? undefined         // undefined
                        : coerce_types[val] !== undefined ? coerce_types[val] // true, false, null
                        : val;                                                // string
                }

                if (keys_last) {
                    // Complex key, build deep object structure based on a few rules:
                    // * The 'cur' pointer starts at the object top-level.
                    // * [] = array push (n is set to array length), [n] = array if n is
                    //   numeric, otherwise object.
                    // * If at the last keys part, set the value.
                    // * For each keys part, if the current level is undefined create an
                    //   object or array based on the type of the next keys part.
                    // * Move the 'cur' pointer to the next level.
                    // * Rinse & repeat.
                    for (; i <= keys_last; i++) {
                        key = keys[i] === '' ? cur.length : keys[i];
                        cur = cur[key] = i < keys_last
                            ? cur[key] || ( keys[i + 1] && isNaN(keys[i + 1]) ? {} : [] )
                            : val;
                    }

                } else {
                    // Simple key, even simpler rules, since only scalars and shallow
                    // arrays are allowed.

                    if ($.isArray(obj[key])) {
                        // val is already an array, so push on the next value.
                        obj[key].push(val);

                    } else if (obj[key] !== undefined) {
                        // val isn't an array, but since a second value has been specified,
                        // convert val into an array.
                        obj[key] = [ obj[key], val ];

                    } else {
                        // val is a scalar.
                        obj[key] = val;
                    }
                }

            } else if (key) {
                // No value was defined, so set something meaningful.
                obj[key] = coerce
                    ? undefined
                    : '';
            }
        });

        return obj;
    };


    return {
        log: log,
        confirmAndCall: confirmAndCall,
        showError: showError,
        getCookie: getCookie,
        tooltips: tooltips,
        values: values,
        deparam: deparam
    }

});