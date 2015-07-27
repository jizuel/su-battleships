# android junit tests #

Тестовия клас, който създавате трябва да наследява _android.test.ActivityInstrumentationTestCase2_ . Защо ..2?
...2 ни дава повече възможности (http://developer.android.com/reference/android/test/ActivityInstrumentationTestCase2.html):
  * You can run any test method on the UI thread (see UiThreadTest).
  * You can inject custom Intents into your Activity (see setActivityIntent(Intent)).
А и _ActivityInstrumentationTestCase_ е depricated :).

Трябва да се създаде конструктор без аргументи и от него да се извика super(<пакет>, <тестово activity>).
> super("com.su.android.battleship.ui", PreferencesMenuScreen.class);
После си пишете test case-овете.


За да се пуснат тестове под android те трябва да се сложат в _android.test.InstrumentationTestRunner_.
Трябва да се override-не _public TestSuite getAllTests()_ и в него се указват тестовите класове.
Един такъв клас изпълнява всички тестове в него.
Естествено всяки тестов клас може да се изпълни и самостоятелно.
Когато създадем нашия си _InstrumentationTestRunner_ трябва да го добавим в конфигурационния xml файл на android:
> <instrumentation android:label="Preferences Tests"
> > android:name="com.su.android.battleship.unittests.preferences.PreferencesInstrumentationTestRunner"
> > android:targetPackage="com.su.android.battleship" />

, като указваме някакъв етикет, името на класа и пакет, за който ще се изпълнява този клас.
Също в конфигурационния файл трябва да се укаже, че приложението използва android.test.runner:

> <uses-library android:name="android.test.runner" />

После от eclipse за тестов клас трябва да се отиде в run configuration -> android junit test -> instrumentation runner и да се
избере нашият си runner от комбобокса (трябва да се е появил в комбобокса, ако не е праснете 1 **rebuild/clean** и се молете :D).

Всеки тестов клас трябва да си има runner. А самият runner клас, за да се пуска трябва пак от run configuration -> android junit test
да се избере втория радио бутон - run all tests in selected project, or package

Има работещи тестове в com.su.android.battleship.unittests.preferences