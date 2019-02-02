package cn.cdyxtech.lab.constain;

public interface ECOOption {

    class ChainNode{
        Long chainId;
        Long ecmId;

        public Long getChainId() {
            return chainId;
        }

        public void setChainId(Long chainId) {
            this.chainId = chainId;
        }

        public Long getEcmId() {
            return ecmId;
        }

        public void setEcmId(Long ecmId) {
            this.ecmId = ecmId;
        }
    }
}
