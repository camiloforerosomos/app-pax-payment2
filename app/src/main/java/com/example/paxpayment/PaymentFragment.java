package com.example.paxpayment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.paxpayment.service.PosLinkSingleton;
import com.example.paxpayment.util.LoadingDialog;
import com.pax.poslink.PaymentRequest;
import com.pax.poslink.PosLink;
import com.pax.poslink.ProcessTransResult;
import com.pax.poslink.base.BaseRequest;
import com.pax.poslink.peripheries.POSLinkPrinter;
import com.pax.poslink.peripheries.ProcessResult;

public class PaymentFragment extends Fragment {
    private static final String TAG = "Tab1Frament";

    private Button btnPayment;
    private Spinner spPaymentEDCType;
    private Spinner spPaymentTransType;
    private EditText etPaymentAmount;

    private PosLink link;
    private LoadingDialog loadingDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.payment_fragment, container, false);

        // Init link:

        link = (PosLink) getArguments().getSerializable("link_instance");

        btnPayment = view.findViewById(R.id.btnPayment);

        spPaymentEDCType = view.findViewById(R.id.spPaymentEDCType);
        spPaymentTransType = view.findViewById(R.id.spPaymentTransType);
        etPaymentAmount = view.findViewById(R.id.etPaymentAmount);

        String[] edcTypes = new String[]{"CREDIT", "DEBIT", "CHECK", "CASH"};
        String[] transTypes = new String[]{"SALE", "RETURN", "VOID"};

        ArrayAdapter<String> adapterEdc = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_spinner_dropdown_item, edcTypes);
        ArrayAdapter<String> adapterType = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_spinner_dropdown_item, transTypes);

        spPaymentEDCType.setAdapter(adapterEdc);
        spPaymentTransType.setAdapter(adapterType);

        loadingDialog = new LoadingDialog(getActivity());

        btnPayment.setOnClickListener(v -> {

            int pyEDCType = 0;
            switch (spPaymentEDCType.getSelectedItem().toString()) {
                case "CREDIT":
                    pyEDCType = 1;
                    break;
                case "DEBIT":
                    pyEDCType = 2;
                    break;
                case "CHECK":
                    pyEDCType = 3;
                    break;
                case "CASH":
                    pyEDCType = 8;
                    break;
                default:
                    break;
            }

            int pyTransType = 0;
            switch (spPaymentTransType.getSelectedItem().toString()) {
                case "SALE":
                    pyTransType = 2;
                    break;
                case "RETURN":
                    pyTransType = 3;
                    break;
                case "VOID":
                    pyTransType = 4;
                    break;
                default:
                    break;
            }

            String amount = etPaymentAmount.getText().toString();

            PaymentRequest paymentRequest = new PaymentRequest();

            paymentRequest.TenderType = pyEDCType;
            paymentRequest.TransType = pyTransType;
            paymentRequest.Amount = amount;

            link = PosLinkSingleton.getLinkInstance(getActivity().getApplicationContext());

            link.PaymentRequest = paymentRequest;

            POSLinkPrinter printer = POSLinkPrinter.getInstance(getActivity().getApplicationContext());
            printer.print("Probando los pros B)", POSLinkPrinter.CutMode.DO_NOT_CUT, new POSLinkPrinter.PrintListener() {
                @Override
                public void onSuccess() {
                    System.out.println("success");
                }

                @Override
                public void onError(ProcessResult processResult) {
                    System.out.println("Error");
                }
            }, true);

            loadingDialog.startLoadingDialog();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    loadingDialog.dismissDialog();
                }
            }, 5000);

            new asyncRequest().execute(paymentRequest);
            // Toast.makeText(getActivity(), "Testeando Payment1", Toast.LENGTH_SHORT).show();

        });

        return view;
    }

    private class asyncRequest extends AsyncTask<BaseRequest, Integer, ProcessTransResult> {

        @Override
        protected ProcessTransResult doInBackground(BaseRequest... baseRequests) {

            BaseRequest request = baseRequests[0];

            // Do the thing
            ProcessTransResult result = link.ProcessTrans();

            Log.d("Report: ", String.valueOf(link.GetReportedStatus()));

            return result;
        }

        @Override
        protected void onPostExecute(ProcessTransResult result) {

            if(result.Code == ProcessTransResult.ProcessTransResultCode.OK) {
                Log.d("PaxResponse: ", String.valueOf(link.GetReportedStatus()));

                Log.d("Transaccion Estado: ", link.PaymentResponse.ResultCode);


            } else if(result.Code == ProcessTransResult.ProcessTransResultCode.ERROR) {
                Log.d("PaxResponse: ", String.valueOf(link.GetReportedStatus()));
            } else if(result.Code == ProcessTransResult.ProcessTransResultCode.TimeOut) {
                Log.d("PaxResponse: ", String.valueOf(link.GetReportedStatus()));
            }

            System.out.println("Code: " + result.Code);
            System.out.println("Message: " + result.Msg);
            // super.onPostExecute(result);
        }
    }
}
