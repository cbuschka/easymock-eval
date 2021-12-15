package easymock_eval.application.retour_processing;

import easymock_eval.application.supplier_charging.ChargeSupplierService;
import easymock_eval.application.customer_refunding.RefundCustomerService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import easymock_eval.domain.retour.Retour;

@NoArgsConstructor
@AllArgsConstructor
public class ProcessRetourService {

    private RetourValidationService retourValidationService;

    private ChargeSupplierService chargeSupplierService;

    private RefundCustomerService refundCustomerService;

    public void processRetour(Retour retour) {

        boolean isRetourValid = retourValidationService.isValid(retour);
        if (isRetourValid) {
            refundCustomerService.refundCustomer(retour.getOrderNo(), retour.getAmountEurCents());
            chargeSupplierService.chargeSupplier(retour.getOrderNo(), retour.getAmountEurCents());
        }
    }
}
