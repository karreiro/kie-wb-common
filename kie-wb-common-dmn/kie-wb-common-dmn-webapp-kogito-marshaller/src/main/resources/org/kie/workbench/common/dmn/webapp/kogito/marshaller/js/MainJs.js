/**
 * == READ ME ==
 *
 * This file has been manually modified to include *ALL* mappings (and not just DMN12)
 *
 * @type {{marshall: MainJs.marshall, unmarshall: MainJs.unmarshall}}
 */

MainJs = {
    initializeMappings: function () {
        function mockDmnMapping(version, namespace, namespaceDmnDi, namespaceDi) {
            var dmnMock = JSON.parse(JSON.stringify(DMN12));
            var dmnDiMock = JSON.parse(JSON.stringify(DMNDI12));

            dmnMock.name = "DMN" + version;
            dmnMock.defaultElementNamespaceURI = namespace;
            dmnMock.dependencies = ["DMNDI" + version];

            (dmnMock.typeInfos || []).map(function (typeInfo) {
                (typeInfo.propertyInfos || []).map(function (propertyInfo) {
                    if (propertyInfo.name === "dmndi") {
                        propertyInfo.elementName.namespaceURI = namespaceDmnDi;
                        propertyInfo.typeInfo = "DMNDI" + version + ".DMNDI";
                    }
                });
            });

            dmnDiMock.name = "DMNDI" + version;
            dmnDiMock.defaultElementNamespaceURI = namespaceDmnDi;

            (dmnDiMock.elementInfos || []).map(function (elementInfo) {
                if (elementInfo.elementName === "DMNStyle") {
                    elementInfo.substitutionHead.namespaceURI = namespaceDi;
                }
            });

            return [dmnMock, dmnDiMock];
        }

        var DMN10 = mockDmnMapping(
                "10",
                "http://www.omg.org/spec/DMN/20130901",
                "http://www.omg.org/spec/DMN/20130901/DMNDI/",
                "http://www.omg.org/spec/DMN/20130901/DI/"
        );

        var DMN11 = mockDmnMapping(
                "11",
                "http://www.omg.org/spec/DMN/20151101/dmn.xsd",
                "http://www.omg.org/spec/DMN/20151101/DMNDI/",
                "http://www.omg.org/spec/DMN/20151101/DI/"
        );

        var DMN13 = mockDmnMapping(
                "13",
                "https://www.omg.org/spec/DMN/20191111/MODEL/",
                "https://www.omg.org/spec/DMN/20191111/DMNDI/",
                "https://www.omg.org/spec/DMN/20191111/DI/"
        );

        return [].concat.apply(
                [DC, DI, DMNDI12, DMN12, KIE],
                [DMN10, DMN11, DMN13]
        );
    },

    _mappings: [],

    mappings: function initializeMappings() {
        if (this._mappings.length === 0) {
            this._mappings = this.initializeMappings();
        }
        return this._mappings;
    },

    isJsInteropConstructorsInitialized: false,

    initializeJsInteropConstructors: function (constructorsMap) {
        if (this.isJsInteropConstructorsInitialized) {
            return;
        }

        this.isJsInteropConstructorsInitialized = true;

        function createFunction(typeName) {
            return new Function('return { "TYPE_NAME" : "' + typeName + '" }');
        }

        function createNoTypedFunction() {
            return new Function("return { }");
        }

        function createConstructor(value) {

            var parsedJson = JSON.parse(value);
            var name = parsedJson["name"];
            var nameSpace = parsedJson["nameSpace"];
            var typeName = parsedJson["typeName"];

            if (nameSpace != null) {
                if (typeName != null) {
                    window[nameSpace][name] = createFunction(typeName);
                } else {
                    window[nameSpace][name] = createNoTypedFunction();
                }
            } else {
                if (typeName != null) {
                    window[name] = createFunction(typeName);
                } else {
                    window[name] = createNoTypedFunction();
                }
            }
        }

        function hasNameSpace(value) {
            return JSON.parse(value)["nameSpace"] != null;
        }

        function hasNotNameSpace(value) {
            return JSON.parse(value)["nameSpace"] == null;
        }

        function iterateValueEntry(values) {
            var baseTypes = values.filter(hasNotNameSpace);
            var innerTypes = values.filter(hasNameSpace);
            baseTypes.forEach(createConstructor);
            innerTypes.forEach(createConstructor);
        }

        function iterateKeyValueEntry(key, values) {
            iterateValueEntry(values);
        }

        for (var property in constructorsMap) {
            if (constructorsMap.hasOwnProperty(property)) {
                iterateKeyValueEntry(property, constructorsMap[property]);
            }
        }

        console.log("JsInterop constructors successfully generated.");
    },

    unmarshall: function (text, dynamicNamespace, callback) {

        function patchParsedModel(obj) {
            for (var k in obj) {
                if (obj.hasOwnProperty(k)) {
                    if (typeof obj[k] === "object" && obj[k] !== null) {
                        patchParsedModel(obj[k]);
                    } else {
                        if (k === "TYPE_NAME") {
                            obj[k] = obj[k]
                                    .replace("DMN11", "DMN12")
                                    .replace("DMNDI11", "DMNDI12")
                                    .replace("DMN13", "DMN12")
                                    .replace("DMNDI13", "DMNDI12");
                        }

                        if (typeof obj[k] === "string") {
                            obj[k] = obj[k].replace(
                                    "http://www.omg.org/spec/DMN/20151101/dmn.xsd",
                                    "http://www.omg.org/spec/DMN/20180521/MODEL/"
                            );
                            obj[k] = obj[k].replace(
                                    "https://www.omg.org/spec/DMN/20191111/MODEL/",
                                    "http://www.omg.org/spec/DMN/20180521/MODEL/"
                            );
                            obj[k] = obj[k].replace(
                                    "http://www.omg.org/spec/DMN/20151101/DMNDI/",
                                    "http://www.omg.org/spec/DMN/20180521/DMNDI/"
                            );
                            obj[k] = obj[k].replace(
                                    "https://www.omg.org/spec/DMN/20191111/DMNDI/",
                                    "http://www.omg.org/spec/DMN/20180521/DMNDI/"
                            );
                        }
                    }
                }
            }
        }

        // Create Jsonix context
        var context = new Jsonix.Context(this.mappings());

        // Create unmarshaller
        var unmarshaller = context.createUnmarshaller();
        var toReturn = unmarshaller.unmarshalString(text);
        var modelURI = toReturn.name.namespaceURI;

        if (!modelURI.match(new RegExp("http://www.omg.org/spec/DMN/20180521/MODEL/", "g"))) {
            patchParsedModel(toReturn);
        }

        callback(toReturn);
    },

    marshall: function (value, namespacesValues, callback) {
        // Create Jsonix context
        var context = new Jsonix.Context(this.mappings(), {
            namespacePrefixes: namespacesValues
        });

        // Create marshaller
        var marshaller = context.createMarshaller();
        var xmlDocument = marshaller.marshalDocument(value);
        if (typeof FormatterJs !== "undefined") {
            var toReturn = FormatterJs.format(xmlDocument);
            callback(toReturn);
        } else {
            var s = new XMLSerializer();
            var toReturn = s.serializeToString(xmlDocument);
            callback(toReturn);
        }
    }
};
