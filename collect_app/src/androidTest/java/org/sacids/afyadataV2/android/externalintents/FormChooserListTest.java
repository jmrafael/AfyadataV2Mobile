package org.sacids.afyadataV2.android.externalintents;

import android.support.test.filters.Suppress;
import android.support.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.sacids.afyadataV2.android.activities.FormChooserList;

import java.io.IOException;

import static org.sacids.afyadataV2.android.externalintents.ExportedActivitiesUtils.testDirectories;

@Suppress
// Frequent failures: https://github.com/opendatakit/collect/issues/796
public class FormChooserListTest {

    @Rule
    public ActivityTestRule<FormChooserList> formChooserListRule =
            new ExportedActivityTestRule<>(FormChooserList.class);

    @Test
    public void formChooserListMakesDirsTest() throws IOException {
        testDirectories();
    }

}
