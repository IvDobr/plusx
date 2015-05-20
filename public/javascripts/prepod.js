function Stud_group (groupId, groupTitle) {
    var self = this;
    self.groupId = groupId;
    self.groupTitle = ko.observable(groupTitle);
}

function Student (studId, studFirstName, studLastName, studPlusCount, studLabCount) {
    var self = this;
    self.studId = studId;
    self.studFirstName      = ko.observable(studFirstName);
    self.studLastName       = ko.observable(studLastName);
    self.studPlusCount      = ko.observable(studPlusCount);
    self.studLabCount       = ko.observable(studLabCount);
}

function Plus (plusId, plusDate) {
    var self = this;
    self.plusId             = plusId;
    self.plusDate           = ko.observable(plusDate);
}

function BD_group (newGroupTitle, newGroupStudents) {
    var self = this;
    self.newGroupTitle = ko.observable(newGroupTitle);
    self.newGroupStudents = ko.observable(newGroupStudents);
}

function Lab (labId, labTitle, labDeathLine, labStudCount) {
    var self = this;
    self.labId = labId;
    self.labTitle = ko.observable(labTitle);
    self.labDeathLine = ko.observable(labDeathLine);
    self.labStudCount = ko.observable(labStudCount);
}

function Labx (doneLabId, doneLabDate, doneLabLab) {
    var self = this;
    self.doneLabId = doneLabId;
    self.doneLabDate = ko.observable(doneLabDate);
    self.doneLabLab = ko.observable(doneLabLab);
}

ViewModelIndex = function() {
    var self = this;
    self.groups                   = ko.observableArray([]);
    self.students                 = ko.observableArray([]);
    self.labs                     = ko.observableArray([]);

    self.selectedGroup            = ko.observable("");
    self.selectedLab              = ko.observable("");

    self.newGroupTitle            = ko.observable("");
    self.newGroupStudents         = ko.observable("");

    self.studNames                = ko.observable("");
    self.groupNewTitle            = ko.observable("");

    self.studFirstName            = ko.observable("");
    self.studLastName             = ko.observable("");
    self.studPlusCount            = ko.observable("");

    self.plusDate                 = ko.observable("");

    self.labTitle                 = ko.observable("");
    self.labDeathLine             = ko.observable("");
    self.labStudCount             = ko.observable("");

    self.newLabTitle              = ko.observable("");
    self.newLabDeathLine          = ko.observable("");

    self.e_studId                 = ko.observable("");
    self.e_studFirstName          = ko.observable("");
    self.e_studLastName           = ko.observable("");

    self.v_studFirstName          = ko.observable("");
    self.v_studLastName           = ko.observable("");

    self.plusx                    = ko.observableArray([]);
    self.labx                     = ko.observableArray([]);

    self.doneLabDate              = ko.observable("");
    self.doneLabLab               = ko.observable("");

    self.studLast                 = ko.observable("");
    self.studFirst                = ko.observable("");

    self.loadGroups = function(){
        self.groups([]);
        jsRoutes.controllers.API.getGroupsJSON().ajax({
            dataType : 'json',
            contentType : 'charset=utf-8',
            success : function(data) {
                var o = data.groupsListJson;
                for (var i = 0; i< o.length; i++){
                    self.groups.push(new Stud_group(o[i].groupId, o[i].groupTitle));
                    console.log("Успешно обработан json запрос. Записи загружены");
                }
            },
            error : function(data) {
                alert("error! "+ data.error);
                console.log('Не могу отправить json запрос');
                console.log(data);
            }
        });
    };

    self.createGroup = function(){
        var new_group = new BD_group (self.newGroupTitle(), self.newGroupStudents());
        var dataJSON = ko.toJSON(new_group);
        jsRoutes.controllers.API.createGroupJSON().ajax({
            dataType    : 'json',
            contentType : 'application/json; charset=utf-8',
            data        : dataJSON,
            success : function(){
                self.loadGroups();
            },
            error : function(result){
                console.log("Error: " + result);
            }
        });
    };

    self.renameGroup = function(){
        var o = {groupId : self.selectedGroup().groupId, groupNewTitle : self.groupNewTitle()};
        var dataJSON = JSON.stringify(o);
        jsRoutes.controllers.API.renameGroupJSON().ajax({
            dataType    : 'json',
            contentType : 'application/json; charset=utf-8',
            data        : dataJSON,
            success : function(result){
                console.log(result);
                var idx = self.groups.indexOf(self.selectedGroup());
                self.loadGroups();
                self.selectedGroup(self.groups()[idx]);
            },
            error : function(result){
                alert("Не удалось пеименовать группу");
                console.log("Error: " + result);
            }
        });
    };

    self.deleteGroup = function(){
        var o = {groupId : self.selectedGroup().groupId};
        var dataJSON = JSON.stringify(o);
        if (confirm("Подтверждаете удление группы?")) {
            jsRoutes.controllers.API.deleteGroupJSON().ajax({
                dataType    : 'json',
                contentType : 'application/json; charset=utf-8',
                data        : dataJSON,
                success : function(result){
                    console.log(result);
                    self.loadGroups();
                },
                error : function(result){
                    alert("Не удалось удалить группу");
                    console.log("Error: " + result);
                }
            });
        } else { }
    };

    self.addStudToGroup = function(){
        //var o = {groupId : self.selectedGroup().groupId, studNames: self.studNames()};
        //var dataJSON = JSON.stringify(o);
        //jsRoutes.controllers.API.addStudToGroupJSON().ajax({
        //    dataType    : 'json',
        //    contentType : 'application/json; charset=utf-8',
        //    data        : dataJSON,
        //    success : function(result){
        //        console.log(result);
        //        self.loadStudents();
        //    },
        //    error : function(result){
        //        alert("Не удалось добавить студентов");
        //        console.log("Error: " + result);
        //    }
        //});

        var o = {groupId : self.selectedGroup().groupId, studLast: self.studLast(), studFirst : self.studFirst()};
        var dataJSON = JSON.stringify(o);
        jsRoutes.controllers.API.addStudToGroupJSON().ajax({
            dataType    : 'json',
            contentType : 'application/json; charset=utf-8',
            data        : dataJSON,
            success : function(result){
                console.log(result);
                self.loadStudents();
                self.studLast("");
                self.studFirst("");
            },
            error : function(result){
                alert("Не удалось добавить студентов");
                console.log("Error: " + result);
            }
        });
    };

    self.loadStudents = function(){
        if (self.selectedGroup()) {
            self.students([]);
            var o = {groupId : self.selectedGroup().groupId};
            var dataJSON = JSON.stringify(o);
            jsRoutes.controllers.API.loadStudentsJSON().ajax({
                dataType    : 'json',
                contentType : 'application/json; charset=utf-8',
                data        : dataJSON,
                success: function (data) {
                    var o = data.studsListJson;
                    for (var i = 0; i < o.length; i++) {
                        self.students.push(new Student(o[i].studId, o[i].studFirstName, o[i].studLastName, o[i].studPlusCount, o[i].studLabCount));
                        console.log("Успешно обработан json запрос. Записи загружены");
                    }
                },
                error: function (data) {
                    alert("error! " + data.error);
                    console.log('Не могу отправить json запрос');
                    console.log(data);
                }
            });
        }
    };

    self.loadLabs = function(){
        if (self.selectedGroup()) {
            self.labs([]);
            var o = {groupId : self.selectedGroup().groupId};
            var dataJSON = JSON.stringify(o);
            jsRoutes.controllers.API.getLabsJSON().ajax({
                dataType    : 'json',
                contentType : 'application/json; charset=utf-8',
                data        : dataJSON,
                success: function (data) {
                    var o = data.labsListJson;
                    for (var i = 0; i < o.length; i++) {
                        self.labs.push(new Lab(o[i].labId, o[i].labTitle, o[i].labDeathLine, o[i].labStudCount));
                        console.log("Успешно обработан json запрос. Записи загружены");
                    }
                },
                error: function (data) {
                    alert("error! " + data.error);
                    console.log('Не могу отправить json запрос');
                    console.log(data);
                }
            });
        }
    };

    self.addLab = function(){
        var o = {groupId : self.selectedGroup().groupId, newLabTitle: self.newLabTitle(), newLabDeathLine : self.newLabDeathLine()};
        var dataJSON = JSON.stringify(o);
        jsRoutes.controllers.API.createLabJSON().ajax({
            dataType    : 'json',
            contentType : 'application/json; charset=utf-8',
            data        : dataJSON,
            success : function(result){
                console.log(result);
                self.loadLabs();
            },
            error : function(result){
                alert("Не удалось создать лабораторную");
                console.log("Error: " + result);
            }
        });};

    self.delLab = function(lab){
        var o = {groupId : self.selectedGroup().groupId, labId : lab.labId};
        var dataJSON = JSON.stringify(o);
        if (confirm("Подтверждаете удление лабораторной?")) {
            jsRoutes.controllers.API.deleteLabJSON().ajax({
                dataType    : 'json',
                contentType : 'application/json; charset=utf-8',
                data        : dataJSON,
                success : function(result){
                    console.log(result);
                    self.labs.remove(lab);
                },
                error : function(result){
                    alert("Не удалось удалить лабораторную");
                    console.log("Error: " + result);
                }
            });
        } else { }
    };

    self.loader = function(){
        self.loadStudents();
        self.loadLabs();
    };

    self.addPlus = function(stud){
        var o = {groupId : self.selectedGroup().groupId, studId : stud.studId};
        var dataJSON = JSON.stringify(o);
        jsRoutes.controllers.API.addPlusJSON().ajax({
            dataType    : 'json',
            contentType : 'application/json; charset=utf-8',
            data        : dataJSON,
            success : function(result){
                console.log(result);
                stud.studPlusCount((parseInt(stud.studPlusCount()) + 1).toString());
            },
            error : function(result){
                alert("Не удалось добавить плюс");
                console.log("Error: " + result);
            }
        });};

    self.doneLab = function(stud) {
        if (self.selectedGroup()) {
            var o = {groupId: self.selectedGroup().groupId, studId: stud.studId, labId: self.selectedLab().labId};
            var dataJSON = JSON.stringify(o);
            jsRoutes.controllers.API.doneLabJSON().ajax({
                dataType: 'json',
                contentType: 'application/json; charset=utf-8',
                data: dataJSON,
                success: function (result) {
                    console.log(result);
                    stud.studLabCount((parseInt(stud.studLabCount()) + 1).toString());
                },
                error: function (result) {
                    alert("Лабораторная уже сдана");
                    console.log("Error: " + result);
                }
            });
        }
    };

    self.studInfo = function(stud) {
        self.e_studId(stud.studId);
        self.v_studFirstName(stud.studFirstName());
        self.v_studLastName(stud.studLastName());
        var o = {groupId: self.selectedGroup().groupId, studId: stud.studId};
        var dataJSON = JSON.stringify(o);
        jsRoutes.controllers.API.loadPlusxJSON().ajax({
            dataType: 'json',
            contentType: 'application/json; charset=utf-8',
            data: dataJSON,
            success: function (result) {
                console.log(result);
                var o = result.plusInfo;
                for (var i = 0; i < o.length; i++) {
                    self.plusx.push(new Plus(o[i].plusId, o[i].plusDate));
                }

                var p = result.labInfo;
                for (var j = 0; j < p.length; j++) {
                    self.labx.push(new Labx(p[j].doneLabId, p[j].doneLabDate, p[j].doneLabLab));
                }
            },
            error: function (result) {
                alert("Неудача");
                console.log("Error: " + result);
            }
        });

        $('#studinfo').modal('show');
    };

    self.editName = function() {
        var o = {groupId: self.selectedGroup().groupId, studId : self.e_studId(), studFirstName : self.e_studFirstName(), studLastName : self.e_studLastName()};
        var dataJSON = JSON.stringify(o);
        jsRoutes.controllers.API.editStudNameJSON().ajax({
            dataType: 'json',
            contentType: 'application/json; charset=utf-8',
            data: dataJSON,
            success: function (result) {
                console.log(result);
                self.loadStudents();
            },
            error: function (result) {
                alert("Неудача");
                console.log("Error: " + result);
            }
        });
    };

    self.delPlus = function(plus) {
        var o = {groupId : self.selectedGroup().groupId, plusId : plus.plusId};
        var dataJSON = JSON.stringify(o);
        if (confirm("Подтверждаете удление плюсика?")) {
            jsRoutes.controllers.API.delPlusJSON().ajax({
                dataType    : 'json',
                contentType : 'application/json; charset=utf-8',
                data        : dataJSON,
                success : function(result){
                    console.log(result);
                    self.plusx.remove(plus);
                },
                error : function(result){
                    alert("Не удалось удалить плюсик");
                    console.log("Error: " + result);
                }
            });
        } else { }
    };

    self.delDoneLab = function(lab) {
        var o = {groupId : self.selectedGroup().groupId, labId : lab.doneLabId};
        var dataJSON = JSON.stringify(o);
        if (confirm("Подтверждаете удление сданной лабораторной?")) {
            jsRoutes.controllers.API.delDoneLabJSON().ajax({
                dataType    : 'json',
                contentType : 'application/json; charset=utf-8',
                data        : dataJSON,
                success : function(result){
                    console.log(result);
                    self.labx.remove(lab);
                },
                error : function(result){
                    alert("Не удалось удалить лабораторную");
                    console.log("Error: " + result);
                }
            });
        } else { }
    };

    self.delStud = function() {
        var o = {groupId : self.selectedGroup().groupId, studId : self.e_studId()};
        var dataJSON = JSON.stringify(o);
        if (confirm("Подтверждаете удление студента?")) {
            jsRoutes.controllers.API.deleteStudentJSON().ajax({
                dataType    : 'json',
                contentType : 'application/json; charset=utf-8',
                data        : dataJSON,
                success : function(result){
                    console.log(result);
                    self.loadStudents();
                },
                error : function(result){
                    alert("Не удалось удалить студента");
                    console.log("Error: " + result);
                }
            });
        } else { }
    };

    self.loadGroups();

};

$( document ).ready(function() {
    ko.applyBindings(new ViewModelIndex());
});