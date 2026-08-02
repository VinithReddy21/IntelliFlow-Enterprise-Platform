import { useMutation } from '@tanstack/react-query';
import { aiApi } from '../api/aiApi';
import { VectorSearchParams } from '../types/ai';

export const useVectorSearch = () => {
  return useMutation({
    mutationFn: (params: VectorSearchParams) => aiApi.vectorSearch(params),
  });
};

export const useRagQuery = () => {
  return useMutation({
    mutationFn: (prompt: string) => aiApi.ragQuery(prompt),
  });
};
