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
var substringMatcher = function(indexPhrases, indexMap) {
    return function findMatches(q, cb) {
        var matches = [];
        var substrRegex = new RegExp(q, 'i');

        for(let i in indexMap){
            for(let j in indexMap[i].phrases){
                if(substrRegex.test(indexPhrases[indexMap[i].phrases[j]])){
                    matches.push({
                        "url": indexPhrases[i],
                        "name": indexPhrases[indexMap[i].title],
                        "txt": indexPhrases[indexMap[i].phrases[j]]
                    });
                }
            }
        }

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
    source: substringMatcher(indexPhrases, indexMap),
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
