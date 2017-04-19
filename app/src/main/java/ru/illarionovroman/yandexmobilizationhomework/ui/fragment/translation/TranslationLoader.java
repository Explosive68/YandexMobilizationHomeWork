package ru.illarionovroman.yandexmobilizationhomework.ui.fragment.translation;


import android.content.Context;

import java.util.List;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import ru.illarionovroman.yandexmobilizationhomework.R;
import ru.illarionovroman.yandexmobilizationhomework.db.DBManager;
import ru.illarionovroman.yandexmobilizationhomework.model.HistoryItem;
import ru.illarionovroman.yandexmobilizationhomework.network.RestApi;


public class TranslationLoader {

    /**
     * The common scheme is as follows: at first, we are trying to get translation from DB,
     * if there is no such translation that we are looking for, then try to get it from the server
     * @param params {@link TranslationParams} which contains all required information
     *                                        to perform translation
     * @return {@link HistoryItem} from DB or network
     */
    public static Single<HistoryItem> loadHistoryItem(final Context context, final RestApi restApi,
                                                      final TranslationParams params) {
        return Single.just(params.getWordToTranslate())
                .flatMap(new Function<String, Single<HistoryItem>>() {
                    @Override
                    public Single<HistoryItem> apply(String word) throws Exception {
                        // Try to find this word in database
                        HistoryItem historyItem = DBManager.getHistoryItemByParams(context, params);
                        if (historyItem == null) {
                            historyItem = new HistoryItem();
                        }
                        return Single.just(historyItem);
                    }
                })
                .flatMap(new Function<HistoryItem, Single<HistoryItem>>() {
                    @Override
                    public Single<HistoryItem> apply(HistoryItem historyItem) throws Exception {
                        if (historyItem.getId() != HistoryItem.UNSPECIFIED_ID) {
                            // If found in DB - just return
                            return Single.just(historyItem);
                        } else {
                            // If not found - do network request
                            return loadHistoryItemFromNetwork(context, restApi, params);
                        }
                    }
                })
                // Do the work with DB and network in background thread
                .subscribeOn(Schedulers.io())
                // Show the result in UI thread
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * Perform network request, transform response to desired HistoryItem. This is reachable
     * only through DB write and read to obtain generated item's ID and Date
     */
    private static Single<HistoryItem> loadHistoryItemFromNetwork(final Context context,
                                                                  final RestApi restApi,
                                                                  TranslationParams params) {
        String langFromTo = buildTranslationLangParam(context,
                params.getLanguageCodeFrom(),
                params.getLanguageCodeTo());

        return restApi.getTranslation(params.getWordToTranslate(), langFromTo, null)
                .map(translationResponse -> {
                    // Pull data from response
                    StringBuilder translationBuilder = new StringBuilder();
                    List<String> translations = translationResponse.getTranslations();
                    for (int i = 0; i < translations.size(); i++) {
                        translationBuilder.append(translations.get(i));
                        if (i != translations.size() - 1) {
                            translationBuilder.append("\n");
                        }
                    }
                    // Create incomplete item (without id and date)
                    HistoryItem item = new HistoryItem(
                            params.getWordToTranslate(),
                            translationBuilder.toString(),
                            params.getLanguageCodeFrom(),
                            params.getLanguageCodeTo()
                    );
                    return item;
                });
    }

    /**
     * Method for building parameter for translation request using currently selected languages.
     * @return E.g.: "ru-en", "en-ru", etc.
     */
    private static String buildTranslationLangParam(Context context,
                                                    String langCodeFrom, String langCodeTo) {
        return context.getString(R.string.translate_query_param_language_from_to,
                langCodeFrom, langCodeTo);
    }
}
