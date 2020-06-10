java --module-path javafx-sdk-11.0.2/lib --add-modules=javafx.controls,javafx.fxml --add-modules javafx.base,javafx.graphics --add-reads javafx.base=ALL-UNNAMED --add-reads javafx.graphics=ALL-UNNAMED -Djava.library.path=./javafx-sdk-11.0.2/lib -Dfile.encoding=UTF-8 -classpath ./:javafx-sdk-11.0.2/lib/*:./jfoenix-9.0.8.jar:./controlsfx-11.0.1.jar sample.Main

