# PayStoreCheckout - **Aplicação de exemplo**

## Importação da Biblioteca

Na aplicação Android que irá utilizar a biblioteca de pagamento Paystore Checkout deve-se adiciona-la em suas dependencias.

1. No build.gradle da aplicação android (app/build.gradle), indicar o caminho do repositório da biblioteca no Github. Isso fará com que o gradle encontre os artefatos da biblioteca na versão indicada.

```
    def githubProperties = new Properties()
    def githubPropertiesFile = file("github.properties")
    githubProperties.load(new FileInputStream(githubPropertiesFile))

    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/paystore/libcheckout")
            credentials {
                username = githubProperties['gpr.usr'] ?: System.getenv("GPR_USER")
                password = githubProperties['gpr.key'] ?: System.getenv("GPR_API_KEY")
            }
        }
    }
```

2. No arquivo github.properties, inserir o usuário do github e seu respectivo token de acesso.

```
gpr.usr = [user]
gpr.key = [access token]
```

3. Em "dependencies", a biblioteca é incluída como dependência da seguinte maneira:

```java
   implementation 'com.phoebus.libraries.checkout:checkout:0.0.0.6'
```

> Em "app/build.gradle", verifique a versão do minSdkVersion. Versão mínima: 19.

4. Sincronize seu projeto, execute o "Build->Clean Project", em seguida "Build->Make Project". Se nenhum erro for exibido o processo de importação foi realizado com sucesso.

5. No arquivo "AndroidManifest.xml", dentro de `<application></application>`, adicione a seguinte `<activity>`:

```xml
<activity
    android:name="com.phoebus.libraries.checkout.LibActivity"
    android:label="@string/app_name"
    android:configChanges="keyboard|keyboardHidden|orientation|screenSize"
    android:windowSoftInputMode="adjustResize">
</activity>
```

### Iniciando a biblioteca

Para iniciar a biblioteca e suas funcionalidades crie um método que faça a comunicação com a biblioteca "PayStoreCheckout". No método, crie uma "Intent" que irá solicitar uma ação na biblioteca, nessa "Intent" passar um "Bundle" com algumas informações obrigatórias, vide descrição das informações.

Descrição

| Nome                  | Tipo   | Obrigatório | Descrição                                       |
| --------------------- | ------ | ----------- | ----------------------------------------------- |
| _email_               | String | Sim         | Email do cliente que irá realizar a compra      |
| _value_               | Long   | Sim         | Valor a pagar em centavos, ex: 2000 = R\$ 20,00 |
| _merchant_payment_id_ | String | Sim         | Identificação de pagamento do comerciante       |
| _order_number_        | String | Sim         | Número do pedido                                |
| _merchant_token_      | String | Sim         | Token do lojista                                |

Abaixo um exemplo de como colocar a biblioteca em atividade e passar os dados para Intent empacotando-os no Bundle.

Exemplo de método:

```java
private void startLib(){
    Intent intent = new Intent(this, com.phoebus.libraries.checkout.LibActivity.class);

    Bundle libInitialProps = new Bundle();
    libInitialProps.putString("email", "lorem@ipsum.com");
    libInitialProps.putLong("value", 5000);
    libInitialProps.putString("merchant_payment_id", "ipsum1234");
    libInitialProps.putString("order_number", "123");
    libInitialProps.putString("merchant_token", "123456789abcd");

    intent.putExtra("libBundle", libInitialProps);

    startActivity(intent);
}
```

> Para iniciar a biblioteca, chame o método _startLib()_. Pode ser no seu MainActivity após o clique de um botão, por exemplo.

## Fluxo do pagamento

### Fluxo com sucesso

O fluxo do pagamento inicia com a chamada da biblioteca "PayStoreCheckout", uma tela é exibida ao operador apresentando algumas informações, como o valor da compra, também é exibido um formulário solicitando informações do cartão, como o número do cartão, validade do cartão, código de verificação do cartão(cvv) e o nome do cliente(igual no cartão).

Após o preenchimento correto das informações solicitadas, a biblioteca faz o carregamento e processamento dessas informações e exibe ao operador o status do pagamento, aprovado ou recusado.

Ao finalizar o processo, a biblioteca é fechada e o App volta ao seu estado inicial.

### Fluxo com erro

#### Cenários de erro

1. Dados inválidos ou falta de algum dado obrigatório no "Bundle" passado na "Intent" que faz a chamada da biblioteca, na tela de carregamento é mostrado o erro: "Problema nos campos";
2. Preenchimento incorreto dos dados do cartão na tela inicial. Não será possivel avançar no processo de pagamento, caso algum dado não passe na validação do formulário.
3. Falta de conexão no processo de carregamento do pagamento. Será exibido ao operador a opção de tentar novamente ou cancelar o pagamento, se a conexão for reestabelecida e o operador escolher tentar novamente o fluxo de pagamento será completado normalmente.

## Compatibilidade de dependências

Caso ocorra erro ao executar a Intent da biblioteca, verifique na nossa árvore de dependências se alguma de _suas dependências usa uma versão mais recente_. É possível listar toda a àrvore de dependêcias da sua aplicação executando o comando "gradle app:dependencies". Uma parte do resultado corresponde à arvore da biblioteca "PayStoreCheckout", como pode ser vista na listagem a seguir:

```
\--- com.phoebus.libraries.checkout:checkout:0.0.0.0
    +--- androidx.appcompat:appcompat:1.0.2
    | +--- androidx.annotation:annotation:1.0.0
    | +--- androidx.core:core:1.0.1
    | | +--- androidx.annotation:annotation:1.0.0
    | | +--- androidx.collection:collection:1.0.0
    | | | \--- androidx.annotation:annotation:1.0.0
    | | +--- androidx.lifecycle:lifecycle-runtime:2.0.0
    | | | +--- androidx.lifecycle:lifecycle-common:2.0.0
    | | | | \--- androidx.annotation:annotation:1.0.0
    | | | +--- androidx.arch.core:core-common:2.0.0
    | | | | \--- androidx.annotation:annotation:1.0.0
    | | | \--- androidx.annotation:annotation:1.0.0
    | | \--- androidx.versionedparcelable:versionedparcelable:1.0.0
    | | +--- androidx.annotation:annotation:1.0.0
    | | \--- androidx.collection:collection:1.0.0 (_)
    | +--- androidx.collection:collection:1.0.0 (_)
    | +--- androidx.cursoradapter:cursoradapter:1.0.0
    | | \--- androidx.annotation:annotation:1.0.0
    | +--- androidx.legacy:legacy-support-core-utils:1.0.0
    | | +--- androidx.annotation:annotation:1.0.0
    | | +--- androidx.core:core:1.0.0 -> 1.0.1 (_)
    | | +--- androidx.documentfile:documentfile:1.0.0
    | | | \--- androidx.annotation:annotation:1.0.0
    | | +--- androidx.loader:loader:1.0.0
    | | | +--- androidx.annotation:annotation:1.0.0
    | | | +--- androidx.core:core:1.0.0 -> 1.0.1 (_)
    | | | +--- androidx.lifecycle:lifecycle-livedata:2.0.0
    | | | | +--- androidx.arch.core:core-runtime:2.0.0
    | | | | | +--- androidx.annotation:annotation:1.0.0
    | | | | | \--- androidx.arch.core:core-common:2.0.0 (_)
    | | | | +--- androidx.lifecycle:lifecycle-livedata-core:2.0.0
    | | | | | +--- androidx.lifecycle:lifecycle-common:2.0.0 (_)
    | | | | | +--- androidx.arch.core:core-common:2.0.0 (_)
    | | | | | \--- androidx.arch.core:core-runtime:2.0.0 (_)
    | | | | \--- androidx.arch.core:core-common:2.0.0 (_)
    | | | \--- androidx.lifecycle:lifecycle-viewmodel:2.0.0
    | | | \--- androidx.annotation:annotation:1.0.0
    | | +--- androidx.localbroadcastmanager:localbroadcastmanager:1.0.0
    | | | \--- androidx.annotation:annotation:1.0.0
    | | \--- androidx.print:print:1.0.0
    | | \--- androidx.annotation:annotation:1.0.0
    | +--- androidx.fragment:fragment:1.0.0
    | | +--- androidx.core:core:1.0.0 -> 1.0.1 (_)
    | | +--- androidx.legacy:legacy-support-core-ui:1.0.0
    | | | +--- androidx.annotation:annotation:1.0.0
    | | | +--- androidx.core:core:1.0.0 -> 1.0.1 (_)
    | | | +--- androidx.legacy:legacy-support-core-utils:1.0.0 (_)
    | | | +--- androidx.customview:customview:1.0.0
    | | | | +--- androidx.annotation:annotation:1.0.0
    | | | | \--- androidx.core:core:1.0.0 -> 1.0.1 (_)
    | | | +--- androidx.viewpager:viewpager:1.0.0
    | | | | +--- androidx.annotation:annotation:1.0.0
    | | | | +--- androidx.core:core:1.0.0 -> 1.0.1 (_)
    | | | | \--- androidx.customview:customview:1.0.0 (_)
    | | | +--- androidx.coordinatorlayout:coordinatorlayout:1.0.0
    | | | | +--- androidx.annotation:annotation:1.0.0
    | | | | +--- androidx.core:core:1.0.0 -> 1.0.1 (_)
    | | | | \--- androidx.customview:customview:1.0.0 (_)
    | | | +--- androidx.drawerlayout:drawerlayout:1.0.0
    | | | | +--- androidx.annotation:annotation:1.0.0
    | | | | +--- androidx.core:core:1.0.0 -> 1.0.1 (_)
    | | | | \--- androidx.customview:customview:1.0.0 (_)
    | | | +--- androidx.slidingpanelayout:slidingpanelayout:1.0.0
    | | | | +--- androidx.annotation:annotation:1.0.0
    | | | | +--- androidx.core:core:1.0.0 -> 1.0.1 (_)
    | | | | \--- androidx.customview:customview:1.0.0 (_)
    | | | +--- androidx.interpolator:interpolator:1.0.0
    | | | | \--- androidx.annotation:annotation:1.0.0
    | | | +--- androidx.swiperefreshlayout:swiperefreshlayout:1.0.0
    | | | | +--- androidx.annotation:annotation:1.0.0
    | | | | +--- androidx.core:core:1.0.0 -> 1.0.1 (_)
    | | | | \--- androidx.interpolator:interpolator:1.0.0 (_)
    | | | +--- androidx.asynclayoutinflater:asynclayoutinflater:1.0.0
    | | | | +--- androidx.annotation:annotation:1.0.0
    | | | | \--- androidx.core:core:1.0.0 -> 1.0.1 (_)
    | | | \--- androidx.cursoradapter:cursoradapter:1.0.0 (_)
    | | +--- androidx.legacy:legacy-support-core-utils:1.0.0 (_)
    | | +--- androidx.annotation:annotation:1.0.0
    | | +--- androidx.loader:loader:1.0.0 (_)
    | | \--- androidx.lifecycle:lifecycle-viewmodel:2.0.0 (_)
    | +--- androidx.vectordrawable:vectordrawable:1.0.1
    | | +--- androidx.annotation:annotation:1.0.0
    | | \--- androidx.core:core:1.0.0 -> 1.0.1 (_)
    | \--- androidx.vectordrawable:vectordrawable-animated:1.0.0
    | +--- androidx.vectordrawable:vectordrawable:1.0.0 -> 1.0.1 (_)
    | \--- androidx.legacy:legacy-support-core-ui:1.0.0 (_)
    +--- androidx.multidex:multidex:2.0.0
    +--- com.facebook.infer.annotation:infer-annotation:0.11.2
    | \--- com.google.code.findbugs:jsr305:3.0.1 -> 3.0.2
    +--- javax.inject:javax.inject:1
    +--- com.facebook.fresco:fresco:2.0.0
    | +--- com.facebook.fresco:fbcore:2.0.0
    | +--- com.facebook.fresco:drawee:2.0.0
    | | +--- com.facebook.fresco:fbcore:2.0.0
    | | \--- com.facebook.fresco:imagepipeline:2.0.0
    | | +--- com.facebook.fresco:imagepipeline-base:2.0.0
    | | | +--- com.facebook.soloader:soloader:0.6.0
    | | | +--- com.parse.bolts:bolts-tasks:1.4.0
    | | | \--- com.facebook.fresco:fbcore:2.0.0
    | | +--- com.facebook.soloader:soloader:0.6.0
    | | +--- com.parse.bolts:bolts-tasks:1.4.0
    | | \--- com.facebook.fresco:fbcore:2.0.0
    | +--- com.facebook.fresco:imagepipeline:2.0.0 (_)
    | +--- com.facebook.fresco:nativeimagefilters:2.0.0
    | | +--- com.facebook.fresco:imagepipeline:2.0.0 (_)
    | | +--- com.facebook.soloader:soloader:0.6.0
    | | +--- com.parse.bolts:bolts-tasks:1.4.0
    | | \--- com.facebook.fresco:fbcore:2.0.0
    | +--- com.facebook.fresco:nativeimagetranscoder:2.0.0
    | | +--- com.facebook.fresco:imagepipeline-base:2.0.0 (_)
    | | +--- com.facebook.soloader:soloader:0.6.0
    | | +--- com.parse.bolts:bolts-tasks:1.4.0
    | | \--- com.facebook.fresco:fbcore:2.0.0
    | \--- com.facebook.soloader:soloader:0.6.0
    +--- com.facebook.fresco:imagepipeline-okhttp3:2.0.0
    | +--- com.squareup.okhttp3:okhttp:3.12.1
    | | \--- com.squareup.okio:okio:1.15.0
    | +--- com.facebook.fresco:fbcore:2.0.0
    | \--- com.facebook.fresco:imagepipeline:2.0.0 (_)
    +--- com.facebook.soloader:soloader:0.6.0
    +--- com.google.code.findbugs:jsr305:3.0.2
    +--- com.squareup.okhttp3:okhttp:3.12.1 (_)
    +--- com.squareup.okhttp3:okhttp-urlconnection:3.12.1
    | \--- com.squareup.okhttp3:okhttp:3.12.1 (_)
    +--- com.squareup.okio:okio:1.15.0
    +--- com.madgag.spongycastle:core:1.56.0.0
    +--- com.madgag.spongycastle:prov:1.56.0.0
    | \--- com.madgag.spongycastle:core:1.56.0.0
    +--- com.madgag.spongycastle:bcpkix-jdk15on:1.56.0.0
    | +--- com.madgag.spongycastle:core:1.56.0.0
    | \--- com.madgag.spongycastle:prov:1.56.0.0 (_)
    +--- com.redmadrobot:inputmask:4.1.0
    +--- org.jetbrains.kotlin:kotlin-stdlib:1.3.21
    | +--- org.jetbrains.kotlin:kotlin-stdlib-common:1.3.21
    | \--- org.jetbrains:annotations:13.0
    +--- com.facebook.fresco:fbcore:2.0.0
    +--- com.facebook.fresco:drawee:2.0.0 (_)
    +--- com.facebook.fresco:nativeimagefilters:2.0.0 (_)
    +--- com.facebook.fresco:imagepipeline:2.0.0 (_)
    +--- com.facebook.fresco:nativeimagetranscoder:2.0.0 (_)
    \--- com.facebook.fresco:imagepipeline-base:2.0.0 (\*)
```
