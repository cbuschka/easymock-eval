package retour;

import easymock_eval.application.retour_processing.ProcessRetourService;
import easymock_eval.application.retour_processing.RetourValidationService;
import easymock_eval.application.supplier_charging.ChargeSupplierService;
import easymock_eval.application.customer_refunding.RefundCustomerService;
import easymock_eval.domain.retour.Retour;
import org.easymock.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.opentest4j.AssertionFailedError;

import static org.easymock.EasyMock.*;

@ExtendWith(EasyMockExtension.class)
class ProcessRetourServiceTest {

    private static final String AN_ORDER_NO = "O1";

    private static final int AN_AMOUNT = 42;

    @Mock
    private RefundCustomerService refundCustomerService;
    @Mock
    private ChargeSupplierService chargeSupplierService;
    @Mock
    private RetourValidationService retourValidationService;
    @Mock(type = MockType.NICE)
    private Retour retour;

    @TestSubject
    private ProcessRetourService processRetourService;

    @Test
    void shouldProcessAValidRetour() {
        givenIsAValidRetour();

        afterwardsCustomerIsRefunded();
        afterwardsSupplierIsCharged();

        processRetourService.processRetour(retour);

        verify(refundCustomerService);
        verify(chargeSupplierService);
        verify(retourValidationService);
    }

    @Test
    void shouldSkipInvalidRetourSilently() {
        givenIsAnInvalidRetour();

        afterwardsNoCustomerIsRefunded();
        afterwardsNoSupplierIsCharged();

        processRetourService.processRetour(retour);

        verify(refundCustomerService);
        verify(chargeSupplierService);
        verify(retourValidationService);
    }

    private void afterwardsNoSupplierIsCharged() {
        chargeSupplierService.chargeSupplier(anyString(), anyInt());
        expectLastCall().andStubThrow(new AssertionFailedError());
        replay(chargeSupplierService);
    }

    private void afterwardsNoCustomerIsRefunded() {
        refundCustomerService.refundCustomer(anyString(), anyInt());
        expectLastCall().andStubThrow(new AssertionFailedError());
        replay(refundCustomerService);
    }


    private void afterwardsCustomerIsRefunded() {
        refundCustomerService.refundCustomer(AN_ORDER_NO, AN_AMOUNT);
        expectLastCall();
        replay(refundCustomerService);
    }

    private void afterwardsSupplierIsCharged() {
        chargeSupplierService.chargeSupplier(AN_ORDER_NO, AN_AMOUNT);
        expectLastCall();
        replay(chargeSupplierService);
    }

    private void givenIsAValidRetour() {
        expect(retour.getOrderNo()).andReturn(AN_ORDER_NO).anyTimes();
        expect(retour.getAmountEurCents()).andReturn(AN_AMOUNT).anyTimes();
        replay(retour);

        expect(retourValidationService.isValid(retour)).andReturn(true);
        replay(retourValidationService);
    }


    private void givenIsAnInvalidRetour() {
        expect(retourValidationService.isValid(retour)).andReturn(false);
        replay(retourValidationService);
    }
}