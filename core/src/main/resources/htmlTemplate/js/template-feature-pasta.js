$("#menu-toggle").click(function(e) {
    e.preventDefault();
    $("#wrapper").toggleClass("toggled");
});

$("#btn-master").click(function(){
    if($('[ng-view]').attr('class').indexOf('col-lg-12') != -1){
        $('[ng-view]').attr('class', 'col-lg-6');
        $('#feature-master').show();
    }else{
        $('[ng-view]').attr('class', 'col-lg-12');
        $('#feature-master').hide();
    }
});

$("#btn-diff").click(function(){
    $('#feature-diff-row').toggle();
});

// TYPE HEAD
var substringMatcher = function(strs) {
    return function findMatches(q, cb) {
        var matches = [];
        var substrRegex = new RegExp(q, 'i');

        $.each(strs, function (featureId, indiceItem) {
            $.each(indiceItem.values, function (i, txt) {
                if (substrRegex.test(txt)) {
                    matches.push({
                        "url": featureId,
                        "name": indiceItem.name,
                        "txt": txt
                    });
                }
            });
        });

        cb(matches);
    };
};

$('#busca').typeahead({
    hint: false
}, {
    limit: 20,
    displayKey: 'txt',
    name: 'txt',
    display: 'txt',
    source: substringMatcher(indice),
    templates: {
        suggestion: Handlebars.compile("<div><p><strong>{{name}}</strong></p>{{txt}}</div>")
    }
}).bind('typeahead:select', function(ev, suggestion) {
    window.location = "#!/feature/" + suggestion.url + '/' + encodeURIComponent(suggestion.txt);
});


function createMenuItem(parent, menu){
    let li = document.createElement('li');
    let a = document.createElement('a');
    a.text = menu.title;
    a.setAttribute('href', menu.url ? '#!/feature/' + menu.url : 'javascript:;');

    if(['NEW', 'DIFFERENT'].indexOf(menu.diff) !== -1){
        let span = document.createElement('span');
        span.className = (menu.diff == 'NEW' ? 'icon-diff-novo' : 'icon-diff-diferente');
        a.prepend(span);
    }

    if(menu.children.length > 0) {
        menuIdx++;
        a.setAttribute('data-toggle', 'collapse');
        a.setAttribute('data-target', '#menu-' + menuIdx);

        let ul = document.createElement('ul');
        ul.id = 'menu-' + menuIdx;
        ul.className = 'collapse';

        for(let item of menu.children){
            createMenuItem(ul, item);
        }

        li.append(a);
        li.append(ul);
    }else{
        li.append(a);
    }

    parent.append(li);
}
