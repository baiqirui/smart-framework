(function () {

    Versions = function () {
    };

    var strict_range = /\[(.*),(.*)\]/;
    var halfopen_right_range = /\[(.*),(.*)\)/;
    var halfopen_left_range = /\((.*),(.*)\]/;
    var qualifiers = ['M', 'RC', 'BUILD-SNAPSHOT', 'RELEASE'];

    Versions.prototype.matchRange = function (range) {
        var strict_match = range.match(strict_range);
        if (strict_match) {
            return function (version) {
                return compareVersions(strict_match[1], version) <= 0
                    && compareVersions(strict_match[2], version) >= 0;
            }
        }
        var hor_match = range.match(halfopen_right_range);
        if (hor_match) {
            return function (version) {
                return compareVersions(hor_match[1], version) <= 0
                    && compareVersions(hor_match[2], version) > 0;
            }
        }
        var hol_match = range.match(halfopen_left_range);
        if (hol_match) {
            return function (version) {
                return compareVersions(hol_match[1], version) < 0
                    && compareVersions(hol_match[2], version) >= 0;
            }
        }

        return function (version) {
            return compareVersions(range, version) <= 0;
        }
    };

    function parseQualifier(version) {
        var qual = version.replace(/\d+/g, "");
        return qualifiers.indexOf(qual) != -1 ? qual : "RELEASE";
    }

    function compareVersions(a, b) {
        var result;

        var versionA = a.split(".");
        var versionB = b.split(".");
        for (var i = 0; i < 3; i++) {
            result = parseInt(versionA[i], 10) - parseInt(versionB[i], 10);
            if (result != 0) {
                return result;
            }
        }
        var aqual = parseQualifier(versionA[3]);
        var bqual = parseQualifier(versionB[3]);
        result = qualifiers.indexOf(aqual) - qualifiers.indexOf(bqual);
        if (result != 0) {
            return result;
        }
        return versionA[3].localeCompare(versionB[3]);
    }

    /**
     * Parse hash bang parameters from a URL as key value object.
     * For repeated parameters the last parameter is effective.
     * If = syntax is not used the value is set to null.
     * #!x&y=3 -> { x:null, y:3 }
     * @param url URL to parse or null if window.location is used
     * @return Object of key -> value mappings.
     * @source https://gist.github.com/zaus/5201739
     */
    hashbang = function (url, i, hash) {
        url = url || window.location.href;

        var pos = url.indexOf('#!');
        if (pos < 0) return [];
        var vars = [], hashes = url.slice(pos + 2).split('&');

        for (i = hashes.length; i--;) {
            hash = hashes[i].split('=');

            vars.push({name: hash[0], value: hash.length > 1 ? hash[1] : null});
        }

        return vars;
    }

    applyParams = function () {
        var params = hashbang();
        $.each(params, function (index, param) {
            var value = decodeURIComponent(param.value);
            switch (param.name) {
                case 'type':
                case 'packaging':
                case 'javaVersion':
                case 'language':
                    $('.' + param.name.toLowerCase() + '-form-group').removeClass("hidden");
                    $('#' + param.name + ' option[value="' + value + '"]').prop('selected', true);
                    $('#' + param.name).change();
                    break;
                case 'groupId':
                case 'artifactId':
                case 'name':
                case 'description':
                case 'packageName':
                    $('.' + param.name.toLowerCase() + '-form-group').removeClass("hidden");
                    $('#' + param.name).val(value);
                    $('#' + param.name).change();
                    break;
            }
        });
    }

}());

$(function () {
    if (navigator.appVersion.indexOf("Mac") != -1) {
        $(".btn-primary").append("<kbd>&#8984; + &#9166;</kbd>");
    }
    else {
        $(".btn-primary").append("<kbd>alt + &#9166;</kbd>");
    }

    var refreshDependencies = function (versionRange) {
        var versions = new Versions();
        $("#dependencies div.checkbox").each(function (idx, item) {
            if (!$(item).attr('data-range') || versions.matchRange($(item).attr('data-range'))(versionRange)) {
                $("input", item).removeAttr("disabled");
                $(item).removeClass("disabled has-error");
            } else {
                $("input", item).prop('checked', false);
                $(item).addClass("disabled has-error");
                $("input", item).attr("disabled", true);
                removeTag($("input", item).val());
            }
        });
    };
    var addTag = function (artifactId, groupId, name) {
        if ($("#starters div[data-id='" + artifactId + "']").length == 0) {
            $("#starters").append("<div class='tag' data-id='" + artifactId + "' data-groupid='" + groupId + "'>" + name +
                "<button type='button' class='close' aria-label='Close'><span aria-hidden='true'>&times;</span></button></div>");
        }
    };
    var removeTag = function (id) {
        $("#starters div[data-id='" + id + "']").remove();
    };
    var initializeSearchEngine = function (engine) {
        $.getJSON("/json/dependencies.json", function (data) {
            engine.clear();
            engine.add(data);
        });
    };
    var generatePackageName = function () {
        var groupId = $("#groupId").val();
        var artifactId = $("#artifactId").val();
        var package = groupId.concat(".").concat(artifactId)
            .replace(/-/g, '');
        $("#packageName").val(package);
    };
    refreshDependencies($("#bootVersion").val());
    $("#type").on('change', function () {
        $("#form").attr('action', $(this.options[this.selectedIndex]).attr('data-action'))
    });
    $("#groupId").on("change", function () {
        generatePackageName();
    });
    $("#artifactId").on('change', function () {
        $("#name").val($(this).val());
        $("#baseDir").attr('value', this.value)
        generatePackageName();
    });
    $(".tofullversion a").on("click", function () {
        $(".full")[0].classList.toggle("hidden");
    });
    var maxSuggestions = 10;
    var starters = new Bloodhound({
        datumTokenizer: Bloodhound.tokenizers.obj.nonword('artifactId', 'groupId', 'name'),
        queryTokenizer: Bloodhound.tokenizers.nonword,
        identify: function (obj) {
            return obj.artifactId;
        },
        sorter: function (a, b) {
            return a.artifactId > b.artifactId;
        },
        cache: false
    });
    initializeSearchEngine(starters);
    $('#autocomplete').typeahead(
        {
            minLength: 1,
            autoSelect: true,
            highlight: true
        }, {
            name: 'starters',
            source: starters,
            limit: maxSuggestions,
            templates: {
                suggestion: function (data) {
                    return "<div><strong>" + data.artifactId + "</strong><br/><small>" + data.groupId + "</small></div>";
                },
                footer: function (search) {
                    if (search.suggestions && search.suggestions.length == maxSuggestions) {
                        return "<div class=\"tt-footer\">More matches, please refine your search</div>";
                    }
                    else {
                        return "";
                    }
                }
            }
        });
    $('#autocomplete').bind('typeahead:select', function (ev, suggestion) {
        var alreadySelected = $("#dependencies input[value='" + suggestion.artifactId + "']").prop('checked');
        if (alreadySelected) {
            removeTag(suggestion.artifactId);
            $("#dependencies input[value='" + suggestion.artifactId + "']").prop('checked', false);
        }
        else {
            addTag(suggestion.artifactId, suggestion.groupId, suggestion.name);
            $("#dependencies input[value='" + suggestion.artifactId + "']").prop('checked', true);
        }
        $('#autocomplete').typeahead('val', '');
    });
    $("#starters").on("click", "button", function () {
        var id = $(this).parent().attr("data-id");
        $("#dependencies input[value='" + id + "']").prop('checked', false);
        removeTag(id);
    });
    $("#dependencies input").bind("change", function () {
        var value = $(this).val()
        if ($(this).prop('checked')) {
            var results = starters.get(value);
            addTag(results[0].artifactId, results[0].groupId, results[0].name);
        } else {
            removeTag(value);
        }
    });
    Mousetrap.bind(['command+enter', 'alt+enter'], function (e) {
        submit();
    });
    var autocompleteTrap = new Mousetrap($("#autocomplete").get(0));
    autocompleteTrap.bind(['command+enter', 'alt+enter'], function (e) {
        submit();
    });
    autocompleteTrap.bind("enter", function (e) {
        if (e.preventDefault) {
            e.preventDefault();
        } else {
            e.returnValue = false;
        }
    });
    applyParams();

    $("button[name='generate-project']").on("click", function () {
        submit();
    });

    function submit() {
        var bootstrapApplicationName = $("<input>").attr("type", "hidden").attr("name", "bootstrapApplicationName").val($("#bootstrapApplicationName").val());
        if (!/^[A-Z][A-Za-z0-9]+$/.test(bootstrapApplicationName.val())) {
            alert("Incorrect ApplicationName format, Please capitalize the first letter and the rest using letters or numbers.(e.g MyApplication).");
            return false;
        }
        $("#form").empty();
        $("#dependencies input:checked").each(function (i) {
            var groupId = $("<input>").attr("type", "hidden").attr("name", "compileDependencies[" + i + "].groupId").val($(this).data("groupid"));
            var artifactId = $("<input>").attr("type", "hidden").attr("name", "compileDependencies[" + i + "].artifactId").val($(this).data("artifactid"));
            $("#form").append(groupId).append(artifactId);
        });
        var packageName = $("<input>").attr("type", "hidden").attr("name", "packageName").val($("#packageName").val());
        var groupId = $("<input>").attr("type", "hidden").attr("name", "groupId").val($("#groupId").val());
        var artifactId = $("<input>").attr("type", "hidden").attr("name", "artifactId").val($("#artifactId").val());
        var frameworkVersion = $("<input>").attr("type", "hidden").attr("name", "frameworkVersion").val($("#frameworkVersion").val());

        /**加入数据库连接信息*/
        var dbUrl = $("<input>").attr("type", "hidden").attr("name", "dbUrl").val($("#dbUrl").val());
        var dbUserName = $("<input>").attr("type", "hidden").attr("name", "dbUserName").val($("#dbUserName").val());
        var dbPassword = $("<input>").attr("type", "hidden").attr("name", "dbPassword").val($("#dbPassword").val());
        var dbName = $("<input>").attr("type", "hidden").attr("name", "dbName").val($("#dbName").val());



        $("#form").append(bootstrapApplicationName).append(packageName).append(groupId).append(artifactId).append(frameworkVersion)
            .append(dbUrl).append(dbUserName).append(dbPassword).append(dbName);
        $("#form").submit();
        return false;
    }
});
