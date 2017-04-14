package ru.illarionovroman.yandexmobilizationhomework.dagger.component;

import javax.inject.Singleton;

import dagger.Component;
import ru.illarionovroman.yandexmobilizationhomework.adapter.LanguageSelectionAdapter;
import ru.illarionovroman.yandexmobilizationhomework.dagger.module.AppContextModule;
import ru.illarionovroman.yandexmobilizationhomework.dagger.module.NetworkModule;
import ru.illarionovroman.yandexmobilizationhomework.ui.fragment.translation.TranslationFragment;

@Singleton
@Component(modules = {AppContextModule.class, NetworkModule.class})
public interface AppComponent {
    void inject(LanguageSelectionAdapter languageSelectionAdapter);
    void inject(TranslationFragment translationFragment);
}
